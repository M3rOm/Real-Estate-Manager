package com.example.realestatemanager.ui.activities

import android.os.Bundle
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.realestatemanager.R.id
import com.example.realestatemanager.R.layout
import com.example.realestatemanager.utils.Utils
import java.text.NumberFormat
import kotlin.math.max

class LoanSimulationActivity : AppCompatActivity() {
    private var amountSeekBar: SeekBar? = null
    private var timeSeekBar: SeekBar? = null
    private var amountTextView: TextView? = null
    private var timeTextView: TextView? = null
    private var interestTextView: TextView? = null
    private var months = 0
    private var amount = 0
    private var monthPaymentTextView: TextView? = null
    private var totalPaymentTextView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_loan_simulation)
        setViews()
        setListeners()
        setToolbar()
    }

    private fun setToolbar() {
        val toolbar =
                findViewById<Toolbar>(id.loan_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setViews() {
        amountSeekBar = findViewById(id.amount_seek_bar)
        timeSeekBar = findViewById(id.time_seek_bar)
        amountTextView = findViewById(id.amount_text_view)
        timeTextView = findViewById(id.time_text_view)
        interestTextView = findViewById(id.interest_text_view)
        totalPaymentTextView = findViewById(id.payment_total_text_view)
        monthPaymentTextView = findViewById(id.month_payment_text_view)
    }

    private fun setListeners() {
        amountSeekBar!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                    seekBar: SeekBar,
                    progress: Int,
                    fromUser: Boolean
            ) {
                amount = progress
                updateValues()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        timeSeekBar!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                    seekBar: SeekBar,
                    progress: Int,
                    fromUser: Boolean
            ) {
                months = progress
                updateValues()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun updateValues() {
        amountTextView!!.text = NumberFormat.getInstance().format(amount.toLong())
        timeTextView!!.text = months.toString()
        val interestRate = getInterestRate(months)
        val totalPayment = getTotalPayment(interestRate, amount)
        val monthlyPayment = getMonthlyPayment(totalPayment, months)
        interestTextView!!.text = Utils.formatDoubleToString(interestRate)
        totalPaymentTextView!!.text = Utils.formatDoubleToString(
                totalPayment
        )
        monthPaymentTextView!!.text = Utils.formatDoubleToString(
                monthlyPayment
        )
    }

    companion object {
        fun getInterestRate(months: Int): Double {
            val factor = 9.0
            return if (months < 1) {
                0.0
            } else max(months / factor, 3.0)
        }

        fun getTotalPayment(interestRate: Double, amount: Int): Double {
            return interestRate / 100 * amount + amount
        }

        fun getMonthlyPayment(totalPayment: Double, months: Int): Double {
            return totalPayment / months
        }
    }
}