package com.example.realestatemanager

import android.content.Context
import com.example.realestatemanager.ui.activities.LoanSimulationActivity.Companion.getInterestRate
import com.example.realestatemanager.ui.activities.LoanSimulationActivity.Companion.getMonthlyPayment
import com.example.realestatemanager.ui.activities.LoanSimulationActivity.Companion.getTotalPayment
import com.example.realestatemanager.utils.Constants.InternetType
import com.example.realestatemanager.utils.Utils.convertDollarToEuro
import com.example.realestatemanager.utils.Utils.formatDate
import com.example.realestatemanager.utils.Utils.internetType
import com.example.realestatemanager.utils.Utils.isInternetAvailable
import org.junit.*
import org.mockito.*
import java.util.Calendar

class UnitTests {

    @Test
    fun convertDollarToEuro_10dollars() {
        val dollars = 10
        val euros: Int = convertDollarToEuro(dollars)
        Assert.assertEquals(9, euros.toLong())
    }

    @Test
    fun convertDollarToEuro_2dollars() {
        val dollars = 2
        val euros: Int = convertDollarToEuro(dollars)
        Assert.assertEquals(2, euros.toLong())
    }

    @Test
    fun convertDollarToEuro_213213dollars() {
        val dollars = 213213
        val euros: Int = convertDollarToEuro(dollars)
        Assert.assertEquals(191892, euros.toLong())
    }

    @Test
    fun convertDollarToEuro_0dollars() {
        val dollars = 0
        val euros: Int = convertDollarToEuro(dollars)
        Assert.assertEquals(0, euros.toLong())
    }

    @Test
    fun convertDollarToEuro_negative120dollars() {
        val dollars = -120
        val euros: Int = convertDollarToEuro(dollars)
        Assert.assertEquals(-108, euros.toLong())
    }

    @Test
    fun formatDate_size() {
        val calendar = Calendar.getInstance()
        val formattedDate: String = formatDate(calendar)
        val length = formattedDate.length
        Assert.assertEquals(10, length.toLong())
    }

    @Test
    fun interestRate_78months() {
        val calendar = Calendar.getInstance()
        val formattedDate: String = formatDate(calendar)
        val length = formattedDate.length
        Assert.assertEquals(10, length.toLong())
    }

    @Test
    fun interestRate_10months() {
        val months = 10
        val interestRate: Double = getInterestRate(months)
        Assert.assertEquals(3.0, interestRate, 0.001)
    }

    @Test
    fun interestRate_14months() {
        val months = 14
        val interestRate: Double = getInterestRate(months)
        Assert.assertEquals(3.0, interestRate, 0.001)
    }

    @Test
    fun interestRate_121months() {
        val months = 121
        val interestRate: Double = getInterestRate(months)
        Assert.assertEquals(13.444, interestRate, 0.001)
    }

    @Test
    fun interestRate_0months() {
        val months = 0
        val interestRate: Double = getInterestRate(months)
        Assert.assertEquals(0.0, interestRate, 0.001)
    }

    @Test
    fun totalPayment_3rate_20m() {
        val interestRate = 3.0
        val amount = 20000
        val totalPayment: Double = getTotalPayment(interestRate, amount)
        Assert.assertEquals(20600.0, totalPayment, 0.001)
    }

    @Test
    fun totalPayment_5rate_48m() {
        val interestRate = 5.0
        val amount = 48000
        val totalPayment: Double = getTotalPayment(interestRate, amount)
        Assert.assertEquals(50400.0, totalPayment, 0.001)
    }

    @Test
    fun monthlyPayment_50400amount_18months() {
        val totalPayment = 50400.0
        val months = 18
        val monthlyPayment: Double = getMonthlyPayment(totalPayment, months)
        Assert.assertEquals(2800.0, monthlyPayment, 0.001)
    }

    @Test
    fun monthlyPayment_120000amount_96months() {
        val totalPayment = 120000.0
        val months = 96
        val monthlyPayment: Double =
            getMonthlyPayment(totalPayment, months)
        Assert.assertEquals(1250.0, monthlyPayment, 0.001)
    }

    @Test
    fun internetTypeNone() {
        val context =
            Mockito.mock(Context::class.java)
        val internetType = internetType(context)
        Assert.assertEquals(
            InternetType.INTERNET_NONE,
            internetType
        )
    }

    @Test
    fun isInternetEnable(){
            val context =
                Mockito.mock(Context::class.java)
            val internetAvailable =
                isInternetAvailable(context)
            Assert.assertFalse(internetAvailable)
        }
}