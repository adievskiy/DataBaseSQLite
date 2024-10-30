package com.example.databasesqlite

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DBActivity : AppCompatActivity() {

    private lateinit var toolbarDB: Toolbar
    private lateinit var nameET: EditText
    private lateinit var phoneET: EditText
    private lateinit var positionSPNR: Spinner
    private lateinit var nameTV: TextView
    private lateinit var phoneTV: TextView
    private lateinit var positionTV: TextView
    private lateinit var saveBTN: Button
    private lateinit var loadBTN: Button
    private lateinit var eraseBTN: Button

    private val db = DBHelper(this, null)
    private val positionList = listOf(
        "Директор",
        "Бухгалтер",
        "Менеджер",
        "Аналитик",
        "Охранник"
    )

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dbactivity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        init()

        saveBTN.setOnClickListener {
            if (nameET.text.isEmpty() || phoneET.text.isEmpty()) {
                Toast.makeText(this,"Заполните все поля", Toast.LENGTH_LONG).show()
            } else {
                val name = nameET.text.toString()
                val phone = phoneET.text.toString()
                val position = positionSPNR.getSelectedItem().toString()
                if (isValidPhoneNumber(phone)) {
                    db.addEmployee(name, phone, position)

                    nameET.text.clear()
                    phoneET.text.clear()
                } else {
                    Toast.makeText(this,"Номер некорректен", Toast.LENGTH_LONG).show()
                }
            }
        }

        loadBTN.setOnClickListener {
            nameTV.text = ""
            phoneTV.text = ""
            positionTV.text = ""
            val cursor = db.getInfo()
            if (cursor != null && cursor.moveToFirst()) {
                cursor.moveToFirst()
                nameTV.append(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_NAME)) + "\n")
                phoneTV.append(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_PHONE)) + "\n")
                positionTV.append(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_POSITION)) + "\n")
            }
            while (cursor!!.moveToNext()) {
                nameTV.append(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_NAME)) + "\n")
                phoneTV.append(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_PHONE)) + "\n")
                positionTV.append(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_POSITION)) + "\n")
            }
            cursor.close()
        }

        eraseBTN.setOnClickListener {
            db.removeDB()
            nameTV.text = ""
            phoneTV.text = ""
            positionTV.text = ""
            Toast.makeText(this, "База данных очищена", Toast.LENGTH_LONG).show()
        }
    }

    private fun init() {
        toolbarDB = findViewById(R.id.toolbarDB)
        setSupportActionBar(toolbarDB)
        title = "База данных"

        nameET = findViewById(R.id.nameET)
        phoneET = findViewById(R.id.phoneET)
        positionSPNR = findViewById(R.id.positionSPNR)
        val adapter = ArrayAdapter(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            positionList
        )
        positionSPNR.setAdapter(adapter)

        nameTV = findViewById(R.id.nameTV)
        phoneTV = findViewById(R.id.phoneTV)
        positionTV = findViewById(R.id.positionTV)
        saveBTN = findViewById(R.id.saveBTN)
        loadBTN = findViewById(R.id.loadBTN)
        eraseBTN = findViewById(R.id.eraseBTN)
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        val regex = Regex("^(8\\d{10}|\\+7\\d{10})$")
        return regex.matches(phone)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.exitMenu -> finishAffinity()
        }
        return super.onOptionsItemSelected(item)
    }
}