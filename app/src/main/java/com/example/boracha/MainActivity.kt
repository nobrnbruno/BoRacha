package com.example.boracha

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import java.util.*

class MainActivity : AppCompatActivity() , TextWatcher, TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private lateinit var edtConta: EditText
    private lateinit var edtQtd: EditText
    private lateinit var result: TextView
    private var ttsSucess: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edtConta = findViewById(R.id.edtConta)
        edtQtd = findViewById(R.id.edtQtd)
        result = findViewById(R.id.result)

        edtConta.addTextChangedListener(this)
        edtQtd.addTextChangedListener(this)

        tts = TextToSpeech(this, this)

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        Log.d("PDM24","Antes de mudar")
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        Log.d("PDM24","Mudando")
    }

    override fun afterTextChanged(s: Editable?) {
        val param1 = edtConta.text.toString().toFloatOrNull()
        val param2 = edtQtd.text.toString().toFloatOrNull()

        if (param1 != null && param2 != null && param2 != 0f) {
            divide(param1, param2)
        } else {
            result.text = "0,00" // Default display
        }
    }

    private fun divide(param1: Float, param2: Float){
        val division: Float = (param1/param2)

        val formattedResult = String.format(Locale("pt", "BR"), "%.2f", division).replace('.', ',')

        result.text = formattedResult
    }

    fun clickSpeak(v: View){
        val split = result.text.split(',')
        val reaisValue = split[0] // Part before the comma
        val centavosValue = split[1] // Part after the comma

        if (tts.isSpeaking) {
            tts.stop()
        }
        if(ttsSucess) {
            Log.d ("PDM23", tts.toString())
            tts.speak("$reaisValue reais e $centavosValue centavos", TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun shareText(v: View) {
        val message = "\uD83D\uDCB0 Total a rachar: R\$${edtConta.text.toString()}\n\uD83D\uDC65 Pessoas: ${edtQtd.text.toString()}\n💸 Divisão da conta: R\$${result.text.toString()}"


        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Compartilhar mensagem")
        startActivity(shareIntent)
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.getDefault()
            ttsSucess = true
            Log.d("PDM23", "Sucesso na Inicialização")
        } else {
            Log.e("PDM23", "Failed to initialize TTS engine.")
            ttsSucess = false
        }
    }
}