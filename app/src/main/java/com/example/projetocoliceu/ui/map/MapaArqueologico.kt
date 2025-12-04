package com.example.projetocoliceu.ui.map

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.projetocoliceu.data.model.Artefato
import com.example.projetocoliceu.viewmodel.MapViewModel
import android.graphics.Bitmap
import android.graphics.BitmapFactory
// Esta classe herda de View e serve como o seu mapa cartesiano customizado.

class MapaArqueologico(context: Context, attrs: AttributeSet?) : View(context, attrs){

    private var backgroundBitmap: Bitmap? = null // <-- Nova variável
    // O ViewModel será injetado pela Activity
    fun setBackgroundImage(bitmap: Bitmap) {
        backgroundBitmap = bitmap
        invalidate() // redesenha a View
    }
    private lateinit var viewModel: MapViewModel

    var onArtefatoClick: ((Artefato) -> Unit)? = null
    var onNovoArtefatoClick: ((String, Float, Float) -> Unit)? = null

    private var artefatos: List<Artefato> = emptyList()

    private val NUM_COLUNAS = 7
    private val NUM_LINHAS = 9

    private val gridPaint = Paint().apply {
        color = Color.DKGRAY
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }

    private val markerPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 30f
    }

    fun setViewModel(viewModel: MapViewModel) {
        this.viewModel = viewModel
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        backgroundBitmap?.let {
            canvas.drawBitmap(
                Bitmap.createScaledBitmap(it, width, height, true),
                0f,
                0f,
                null
            )
        }

        val quadraPixelLargura = width.toFloat() / NUM_COLUNAS
        val quadraPixelAltura = height.toFloat() / NUM_LINHAS

        drawGrid(canvas, quadraPixelLargura, quadraPixelAltura)
        drawArtefatos(canvas, quadraPixelLargura, quadraPixelAltura)
    }

    private fun drawGrid(canvas: Canvas, quadraPixelLargura: Float, quadraPixelAltura: Float) {
        for (i in 0..NUM_COLUNAS) {
            val x = i * quadraPixelLargura
            canvas.drawLine(x, 0f, x, height.toFloat(), gridPaint)
        }
        for (j in 0..NUM_LINHAS) {
            val y = j * quadraPixelAltura
            canvas.drawLine(0f, y, width.toFloat(), y, gridPaint)
        }

        for (col in 0 until NUM_COLUNAS) {
            for (row in 0 until NUM_LINHAS) {
                val quadraLabel = "${'A' + col}${row + 1}"
                val textX = col * quadraPixelLargura + 5f
                val textY = row * quadraPixelAltura + 30f
                canvas.drawText(quadraLabel, textX, textY, textPaint)
            }
        }
    }

    fun setArtefatos(newArtefatos: List<Artefato>) {
        this.artefatos = newArtefatos
        invalidate()
    }

    private fun drawArtefatos(canvas: Canvas, quadraPixelLargura: Float, quadraPixelAltura: Float) {
        artefatos.forEach { artefato ->
            // 🔥 PROTEÇÃO: Verifica se a quadra tem conteúdo válido
            if (artefato.quadra.isEmpty()) return@forEach

            val colunaIndex = artefato.quadra.firstOrNull()?.let { it.uppercaseChar() - 'A' } ?: 0

            // 🔥 CORREÇÃO: Verifica se tem pelo menos 2 caracteres antes de fazer substring
            val linhaIndex = if (artefato.quadra.length > 1) {
                artefato.quadra.substring(1).toIntOrNull()?.minus(1) ?: 0
            } else {
                0
            }

            val quadraInicioX = colunaIndex * quadraPixelLargura
            val quadraInicioY = linhaIndex * quadraPixelAltura

            val artefatoPixelX = quadraInicioX + (artefato.xRelativo * quadraPixelLargura)
            val artefatoPixelY = quadraInicioY + (artefato.yRelativo * quadraPixelAltura)

            canvas.drawCircle(artefatoPixelX, artefatoPixelY, 15f, markerPaint)
            canvas.drawText(artefato.area, artefatoPixelX + 20, artefatoPixelY + 10, textPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val touchX = event.x
            val touchY = event.y

            val quadraPixelLargura = width.toFloat() / NUM_COLUNAS
            val quadraPixelAltura = height.toFloat() / NUM_LINHAS

            // 1. Verificar clique em artefato existente
            val artefatoClicado = artefatos.firstOrNull { artefato ->
                // 🔥 PROTEÇÃO: Verifica se a quadra é válida
                if (artefato.quadra.isEmpty()) return@firstOrNull false

                val colunaIndex = artefato.quadra.firstOrNull()?.let { it.uppercaseChar() - 'A' } ?: 0

                // 🔥 CORREÇÃO: Verifica comprimento antes de substring
                val linhaIndex = if (artefato.quadra.length > 1) {
                    artefato.quadra.substring(1).toIntOrNull()?.minus(1) ?: 0
                } else {
                    0
                }

                val quadraInicioX = colunaIndex * quadraPixelLargura
                val quadraInicioY = linhaIndex * quadraPixelAltura
                val artefatoPixelX = quadraInicioX + (artefato.xRelativo * quadraPixelLargura)
                val artefatoPixelY = quadraInicioY + (artefato.yRelativo * quadraPixelAltura)

                val distanciaX = touchX - artefatoPixelX
                val distanciaY = touchY - artefatoPixelY
                val raioQuadrado = 15f * 15f
                (distanciaX * distanciaX + distanciaY * distanciaY) < raioQuadrado
            }

            if (artefatoClicado != null) {
                onArtefatoClick?.invoke(artefatoClicado)
                return true
            }

            // 2. Criar novo artefato via toque no mapa
            val colunaIndex = (touchX / quadraPixelLargura).toInt()
            val linhaIndex = (touchY / quadraPixelAltura).toInt()

            // 🔥 PROTEÇÃO: Verifica limites do mapa
            if (colunaIndex < 0 || colunaIndex >= NUM_COLUNAS ||
                linhaIndex < 0 || linhaIndex >= NUM_LINHAS) {
                return super.onTouchEvent(event)
            }

            val xOffset = touchX - (colunaIndex * quadraPixelLargura)
            val yOffset = touchY - (linhaIndex * quadraPixelAltura)

            val xRelativo = xOffset / quadraPixelLargura
            val yRelativo = yOffset / quadraPixelAltura

            val nomeQuadra = "${('A' + colunaIndex)}${linhaIndex + 1}"

            // Chama o callback de criação
            onNovoArtefatoClick?.invoke(nomeQuadra, xRelativo, yRelativo)

            return true
        }
        return super.onTouchEvent(event)
    }
}