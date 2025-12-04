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

    // Callback de clique no artefato dentro do CustomView
    var onArtefatoClick: ((Artefato) -> Unit)? = null

    var onNovoArtefatoClick: ((String, Float, Float) -> Unit)? = null
    // Dados que serão desenhados (observados do ViewModel)
    private var artefatos: List<Artefato> = emptyList()

    // --- CONFIGURAÇÕES DO MAPA (Ajuste esses valores se a grade mudar!) ---
    private val NUM_COLUNAS = 7 // Ex: A, B, C...
    private val NUM_LINHAS = 9  // Ex: 1, 2, 3...
    // ---------------------------------------------------------------------

    // Pincéis para Desenho
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

    // --- MÉTODOS PÚBLICOS PARA INTERAÇÃO COM A ACTIVITY ---

    // 1. Recebe a lista de artefatos observada do ViewModel

    // 2. Recebe a instância do ViewModel
    fun setViewModel(viewModel: MapViewModel) {
        this.viewModel = viewModel
    }

    // --- LÓGICA DE DESENHO ---

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

        // 1. Desenhar a Grade Cartesiana
        drawGrid(canvas, quadraPixelLargura, quadraPixelAltura)

        // 2. Desenhar os Marcadores dos Artefatos
        drawArtefatos(canvas, quadraPixelLargura, quadraPixelAltura)
    }

    private fun drawGrid(canvas: Canvas, quadraPixelLargura: Float, quadraPixelAltura: Float) {
        // Desenha linhas verticais
        for (i in 0..NUM_COLUNAS) {
            val x = i * quadraPixelLargura
            canvas.drawLine(x, 0f, x, height.toFloat(), gridPaint)
        }
        // Desenha linhas horizontais
        for (j in 0..NUM_LINHAS) {
            val y = j * quadraPixelAltura
            canvas.drawLine(0f, y, width.toFloat(), y, gridPaint)
        }

        // Adiciona rótulos das quadras (A1, A2, ..., B1, B2, ...)
        for (col in 0 until NUM_COLUNAS) {
            for (row in 0 until NUM_LINHAS) {
                val quadraLabel = "${'A' + col}${row + 1}"
                val textX = col * quadraPixelLargura + 5f   // pequeno padding
                val textY = row * quadraPixelAltura + 30f  // pequeno padding
                canvas.drawText(quadraLabel, textX, textY, textPaint)
            }
        }
    }

    fun setArtefatos(newArtefatos: List<Artefato>) {
        this.artefatos = newArtefatos
        invalidate() // Força o Android a chamar o onDraw() novamente para desenhar os marcadores
    }

    private fun drawArtefatos(canvas: Canvas, quadraPixelLargura: Float, quadraPixelAltura: Float) {
        artefatos.forEach { artefato ->
            // Simulação de cálculo de índice da quadra (a partir do nome da quadra no Artefato)
            val colunaIndex = artefato.quadra.firstOrNull()?.let { it.uppercaseChar() - 'A' } ?: 0
            val linhaIndex = artefato.quadra.drop(1).toIntOrNull()?.minus(1) ?: 0

            // Posição de início da quadra em pixels
            val quadraInicioX = colunaIndex * quadraPixelLargura
            val quadraInicioY = linhaIndex * quadraPixelAltura

            // Posição final do marcador em pixels (usando xRelativo e yRelativo 0.0 a 1.0)
            val artefatoPixelX = quadraInicioX + (artefato.xRelativo * quadraPixelLargura)
            val artefatoPixelY = quadraInicioY + (artefato.yRelativo * quadraPixelAltura)

            // Desenha o Marcador
            canvas.drawCircle(artefatoPixelX, artefatoPixelY, 15f, markerPaint)

            // Desenha o ID do Artefato
            canvas.drawText(artefato.area, artefatoPixelX + 20, artefatoPixelY + 10, textPaint)
        }
    }

    // --- LÓGICA DE INTERAÇÃO (TOQUE) ---

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val touchX = event.x
            val touchY = event.y

            val quadraPixelLargura = width.toFloat() / NUM_COLUNAS
            val quadraPixelAltura = height.toFloat() / NUM_LINHAS

            // 1. Verificar clique em artefato existente
            val artefatoClicado = artefatos.firstOrNull { artefato ->
                val colunaIndex = artefato.quadra.firstOrNull()?.let { it.uppercaseChar() - 'A' } ?: 0
                val linhaIndex = artefato.quadra.substring(1).toIntOrNull()?.minus(1) ?: 0
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