package com.learn.form_infomation

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONException

class MainActivity : AppCompatActivity() {
    
    
    private lateinit var addressHelper: AddressHelper
    private lateinit var spinnerProvince: Spinner
    private lateinit var spinnerDistrict: Spinner
    private lateinit var spinnerWard: Spinner
    private lateinit var calendarView: CalendarView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        addressHelper = AddressHelper(resources)

        // Tìm các Spinner và CalendarView trong layout
        spinnerProvince = findViewById(R.id.spinnerProvince)
        spinnerDistrict = findViewById(R.id.spinnerDistrict)
        spinnerWard = findViewById(R.id.spinnerWard)
        calendarView = findViewById(R.id.calendarView)

        // Cài đặt CalendarView (ẩn/hiện khi nhấn nút)
        val btnShowCalendar: Button = findViewById(R.id.btnShowCalendar)
        btnShowCalendar.setOnClickListener {
            calendarView.visibility = if (calendarView.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        // Lấy danh sách tỉnh/thành từ JSON
        try {
            val provinces = addressHelper.getProvinces()
            val provinceAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, provinces)
            provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerProvince.adapter = provinceAdapter
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        // Khi chọn tỉnh/thành phố, cập nhật quận/huyện
        spinnerProvince.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val province = spinnerProvince.selectedItem as String
                val districts = addressHelper.getDistricts(province)
                val districtAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, districts)
                districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerDistrict.adapter = districtAdapter
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Khi chọn quận/huyện, cập nhật phường/xã
        spinnerDistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val province = spinnerProvince.selectedItem as String
                val district = spinnerDistrict.selectedItem as String
                val wards = addressHelper.getWards(province, district)
                val wardAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, wards)
                wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerWard.adapter = wardAdapter
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Xử lý nút Submit
        val btnSubmit: Button = findViewById(R.id.btnSubmit)
        btnSubmit.setOnClickListener {
            validateAndSubmit()
        }
    }

    private fun validateAndSubmit() {
        val etMSSV: EditText = findViewById(R.id.etMSSV)
        val etName: EditText = findViewById(R.id.etName)
        val etEmail: EditText = findViewById(R.id.etEmail)
        val etPhone: EditText = findViewById(R.id.etPhone)
        val rgGender: RadioGroup = findViewById(R.id.rgGender)
        val cbAgree: CheckBox = findViewById(R.id.cbAgree)

        // Kiểm tra điều kiện
        if (etMSSV.text.isBlank() || etName.text.isBlank() || etEmail.text.isBlank() || etPhone.text.isBlank() || rgGender.checkedRadioButtonId == -1 || !cbAgree.isChecked) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin và đồng ý với điều khoản", Toast.LENGTH_SHORT).show()
        } else {
            // Xử lý khi thông tin đầy đủ
            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
        }
    }
}