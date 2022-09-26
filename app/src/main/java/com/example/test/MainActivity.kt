package com.example.test
import android.annotation.SuppressLint
import android.app.ActionBar.LayoutParams
import android.graphics.Color
import android.icu.util.TimeUnit
import android.os.Bundle
import android.os.Handler
import android.text.PrecomputedText.Params
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.OutputStream
import java.io.PrintWriter
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.jvm.internal.*
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    /*---------------------------------- Variables declaration -----------------------------------*/

    //Declaring Buttons
    lateinit var addButton : Button
    lateinit var chooseButton : Button

    //Declaring main 2D array
    private val mainList = kotlin.collections.ArrayList<kotlin.collections.ArrayList<String>>()

    //Contain active list number
    var stateList = 0

    /*---------------------------------- Functions declaration -----------------------------------*/

    private fun hideButton(btn: Button): Boolean {
        btn.visibility = View.INVISIBLE
        return true
    }

    private fun refreshList(file: File): Boolean {
        //get the list added
        val mainEntry: TextView = findViewById<TextView>(R.id.main_entry)
        val mainLayout: LinearLayout = findViewById(R.id.main_layout)
        val entry: String = mainEntry.text.toString()

        if (entry != "") {
            mainList[stateList].add(entry)
        }
        mainLayout.removeAllViews()
        for (i in 0 until mainList[stateList].size) {

            val btn = Button(this)
            btn.text = mainList[stateList][i]
            btn.height = 50
            btn.tag = i
            btn.id = i

            btn.setOnLongClickListener {
                mainList[stateList].remove(btn.text)
                hideButton(btn)
                writeCsv(file)
                refreshList(file)
            }
            mainLayout.addView(btn)

        }
        mainEntry.text = ""
        return true
    }

    private fun readCsv(file: File) {

        val content:String = file.readText()
        var j = 0
        var k = 0

        for (i in content) {
            if (i.toString() == "\n") {
                if (j < 8) {
                    j += 1
                }
                k = 0

            } else {
                if (i.toString() == ",") {
                    k += 1
                    while (mainList[j].size != k+1) {
                        mainList[j].add("")
                    }

                } else {
                    mainList[j][k] = "${mainList[j][k]}$i"
                }
            }
            //Toast.makeText(applicationContext,"${mainList[j][k]}",Toast.LENGTH_SHORT).show()
        }
    }

    //Debug : print main List in toasts
    fun printList() {
        for (i in mainList[0]) {
            Toast.makeText(applicationContext, i,Toast.LENGTH_SHORT).show()
        }

    }

    private fun writeCsv(file: File) : Boolean {
        val writer = PrintWriter(file)
        for (i in 0..8) {
            mainList[i].forEach{
                if (it != "") {
                    writer.append(",$it")
                }
            }
            writer.append("\n")
        }
        writer.close()
        return true
    }

    private fun cleanList() {
        for (list in mainList) {
            for (i in 0 until list.size-1) {
                if (list[i] == "") {
                    list.remove(list[i])
                }
            }
        }
    }

    /*-------------------------------------- Main Function ---------------------------------------*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Opening save file
        val fileDirectory = getExternalFilesDir(".")
        val dataFileName = "data.csv"
        val file = File(fileDirectory, dataFileName)
        val newFileCreated :Boolean = file.createNewFile()

        // Fill main array with 9 child arrays
        for (i in 0..8) {
            mainList.add(kotlin.collections.ArrayList<String>())
        }

        //enter the save file content in the main Array
        readCsv(file)

        if(newFileCreated){
            Toast.makeText(applicationContext,"Fichier de sauvegarde créé",Toast.LENGTH_SHORT).show()
        }


        //Binding buttons
        addButton = findViewById(R.id.button_1)
        chooseButton = findViewById(R.id.choose_button)
        chooseButton.visibility = View.INVISIBLE


        //Registering event listener with our buttons
        addButton.setOnClickListener {

            //Remove empty elements
            cleanList()

            refreshList(file)

            if (mainList[stateList].size > 1) {
                chooseButton.visibility = View.VISIBLE
            }
            //Save in file
            writeCsv(file)
        }

        //Choose button event handler
        chooseButton.setOnClickListener {

            cleanList()

            if (mainList[stateList].size > 1) {

                val builder = AlertDialog.Builder(this)
                val validList = ArrayList<String>()

                for (word in mainList[stateList]) {
                    if (word != "") {
                        validList.add(word)
                    }
                }
                //print results
                val res = validList[Random.nextInt(0, validList.size)]
                builder.setTitle("Avec 1 chance sur ${validList.size}, résultat :")
                builder.setMessage(res)
                builder.show()
            }
            writeCsv(file)
        }

        //List buttons event handler
        val buttonList = kotlin.collections.ArrayList<Button>()
        val buttonLayout: LinearLayout = findViewById(R.id.umenu_layout)

        for (i in 0..8) {
            buttonList.add(buttonLayout.getChildAt(i) as Button)
            buttonList[i].setOnClickListener{
                for (j in 0..8) {
                    buttonList[j].setBackgroundColor(Color.rgb(224, 60, 0))
                }
                buttonList[i].setBackgroundColor(Color.CYAN)
                stateList = i
                refreshList(file)
            }

            buttonList[0].setBackgroundColor(Color.CYAN)
        }
        if (mainList[stateList].size > 1) {
            chooseButton.visibility = View.VISIBLE
        }
    cleanList()
    refreshList(file)
    }
}