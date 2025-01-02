package com.example.proyecto911

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast

class CreateAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val etCedula = findViewById<EditText>(R.id.etCedula)
        val etFirstName = findViewById<EditText>(R.id.etFirstName)
        val etLastName = findViewById<EditText>(R.id.etLastName)
        val rgNationality = findViewById<RadioGroup>(R.id.rgNationality)
        val etCellphone = findViewById<EditText>(R.id.etCellphone)
        val etDisabilityType = findViewById<EditText>(R.id.etDisabilityType)
        val spBloodType = findViewById<Spinner>(R.id.spBloodType)
        val etAllergies = findViewById<EditText>(R.id.etAllergies)
        val etIllnesses = findViewById<EditText>(R.id.etIllnesses)

        val etEmergencyName = findViewById<EditText>(R.id.etEmergencyName)
        val etEmergencyLastName = findViewById<EditText>(R.id.etEmergencyLastName)
        val etEmergencyCellphone = findViewById<EditText>(R.id.etEmergencyCellphone)
        val etEmergencyRelation = findViewById<EditText>(R.id.etEmergencyRelation)

        val btnSave = findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            val cedula = etCedula.text.toString()
            val firstName = etFirstName.text.toString()
            val lastName = etLastName.text.toString()
            val nationality = findViewById<RadioButton>(rgNationality.checkedRadioButtonId)?.text.toString()
            val cellphone = etCellphone.text.toString()
            val disabilityType = etDisabilityType.text.toString()
            val bloodType = spBloodType.selectedItem.toString()
            val allergies = etAllergies.text.toString()
            val illnesses = etIllnesses.text.toString()

            val emergencyName = etEmergencyName.text.toString()
            val emergencyLastName = etEmergencyLastName.text.toString()
            val emergencyCellphone = etEmergencyCellphone.text.toString()
            val emergencyRelation = etEmergencyRelation.text.toString()

            if (cedula.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty() && nationality.isNotEmpty() &&
                cellphone.isNotEmpty() && emergencyName.isNotEmpty() && emergencyLastName.isNotEmpty() &&
                emergencyCellphone.isNotEmpty() && emergencyRelation.isNotEmpty()) {

                Toast.makeText(this, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}