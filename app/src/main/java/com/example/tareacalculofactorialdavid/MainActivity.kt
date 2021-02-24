package com.example.tareacalculofactorialdavid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import com.example.tareacalculofactorialdavid.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var number = MutableLiveData<Int>()
    lateinit var job: Job
    lateinit var editTextNumber: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        editTextNumber = binding.editTextNumber
        binding.btnStart.setOnClickListener {
            onStartCommand(editTextNumber.text.toString().toInt())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        runBlocking {
            job.cancel()
        }
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Salir")
        builder.setMessage("¿Quieres salir de la aplicacion?")
        builder.setPositiveButton("SI") { dialog, which ->
            dialog.dismiss()
            finish()
        }
        builder.setNegativeButton("NO") { dialog, which -> dialog.dismiss() }
        val alert = builder.create()
        alert.show()
    }

    private fun onStartCommand(editTextNumber: Int) {
        number.observeForever {
            Toast.makeText(
                applicationContext,
                "Operacion: " + number.value.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
        job = MainScope().launch {
            showNumber(editTextNumber)
        }
    }

    private suspend fun showNumber(editTextNumber: Int) = withContext(Dispatchers.IO) {
        try {
            while (isActive) {
                var factorialNumber = 1
                val num = editTextNumber
                for (i in 2..num) {
                    factorialNumber *= i
                    number.postValue(factorialNumber)
                    Thread.sleep(2000)
                }
                job.cancel()
            }
        } catch (e: CancellationException) {
        } finally {
        }
    }
}