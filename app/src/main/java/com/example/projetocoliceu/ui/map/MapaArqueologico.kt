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

// Esta classe herda de View e serve como o seu mapa cartesiano customizado.
class MapaArqueologico(context: Context, attrs: AttributeSet?) : View(context, attrs){

    // O ViewModel será injetado pela Activity
    private lateinit var viewModel: MapViewModel

    // Callback de clique no artefato dentro do CustomView
    var onArtefatoClick: ((Artefato) -> Unit)? = null

    // Dados que serão desenhados (observados do ViewModel)
    private var artefatos: List<Artefato> = emptyList()

    // --- CONFIGURAÇÕES DO MAPA (Ajuste esses valores se a grade mudar!) ---
    private val NUM_COLUNAS = 7 // Ex: A, B, C...
    private val NUM_LINHAS = 6  // Ex: 1, 2, 3...
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
    fun setArtefatos(newArtefatos: List<Artefato>) {
        this.artefatos = newArtefatos
        invalidate() // Força o Android a chamar o onDraw() novamente para desenhar os marcadores
    }

    // 2. Recebe a instância do ViewModel
    fun setViewModel(viewModel: MapViewModel) {
        this.viewModel = viewModel
    }

    // --- LÓGICA DE DESENHO ---

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

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
    }

    private fun drawArtefatos(canvas: Canvas, quadraPixelLargura: Float, quadraPixelAltura: Float) {
        artefatos.forEach { artefato ->
            // Simulação de cálculo de índice da quadra (a partir do nome da quadra no Artefato)
            val colunaIndex = artefato.quadra.firstOrNull()?.let { it.uppercaseChar() - 'A' } ?: 0
            val linhaIndex = artefato.quadra.substring(1).toIntOrNull()?.minus(1) ?: 0

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

            // NOVO: 1. Verificar se o toque foi em um artefato existente
            val artefatoClicado = artefatos.firstOrNull { artefato ->
                // Lógica de mapeamento de volta para a posição do marcador (copiado do drawArtefatos)
                val colunaIndex = artefato.quadra.firstOrNull()?.let { it.uppercaseChar() - 'A' } ?: 0
                val linhaIndex = artefato.quadra.substring(1).toIntOrNull()?.minus(1) ?: 0
                val quadraInicioX = colunaIndex * quadraPixelLargura
                val quadraInicioY = linhaIndex * quadraPixelAltura
                val artefatoPixelX = quadraInicioX + (artefato.xRelativo * quadraPixelLargura)
                val artefatoPixelY = quadraInicioY + (artefato.yRelativo * quadraPixelAltura)

                // Simples verificação de raio (15f é o raio usado para desenhar)
                val distanciaX = touchX - artefatoPixelX
                val distanciaY = touchY - artefatoPixelY
                val raioQuadrado = 15f * 15f // Raio para a área de clique
                (distanciaX * distanciaX + distanciaY * distanciaY) < raioQuadrado
            }

            if (artefatoClicado != null) {
                // Se um artefato foi clicado, chama o callback e encerra o evento
                onArtefatoClick?.invoke(artefatoClicado)
                return true
            }

            // 2. Se nenhum artefato foi clicado, continua com a lógica de "novo artefato"
            // ... (o restante da sua lógica original para criar um novo artefato)

            // 1. Descobrir em qual quadra o toque ocorreu
            val colunaIndex = (touchX / quadraPixelLargura).toInt()
            val linhaIndex = (touchY / quadraPixelAltura).toInt()

            // 2. Calcular a posição relativa X e Y (0.0 a 1.0) dentro da quadra
            val xOffset = touchX - (colunaIndex * quadraPixelLargura)
            val yOffset = touchY - (linhaIndex * quadraPixelAltura)

            val xRelativo = xOffset / quadraPixelLargura
            val yRelativo = yOffset / quadraPixelAltura

            // 3. Simulação de Nome da Quadra (Ex: A1, B2)
            val nomeQuadra = "${('A' + colunaIndex)}${linhaIndex + 1}"

            // 4. Chamar a função do ViewModel para iniciar o formulário
            if (::viewModel.isInitialized) {
                // O ViewModel registra o toque e inicia a navegação para o formulário
                viewModel.startNewArtefato(nomeQuadra, xRelativo, yRelativo)
            }


            return true
        }
        return super.onTouchEvent(event)
    }
}