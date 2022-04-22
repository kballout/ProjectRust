package com.example.myapplication

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.myapplication.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var listOfItems = ArrayList<String>()
    private var listOfCategories = ArrayList<String>()
    private var listOfButtons = ArrayList<Button>()
    private var data: ArrayAdapter<String>? = null
    private var navHostFragment: NavHostFragment? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //navigation
        navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        setupActionBarWithNavController(navHostFragment!!.navController)

    }

    //setup navigation controller
    override fun onSupportNavigateUp(): Boolean {
        return navHostFragment!!.navController.navigateUp() || super.onSupportNavigateUp()
    }

    //get list of all categories
    fun getListOfCategories(viewModel: Model){
        listOfCategories.clear()
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            val doc: Document = Jsoup.connect("https://www.corrosionhour.com/rust-items-database/").get()
            val body = doc.getElementById("content-body")
            val categories = body?.getElementsByTag("h2")
            for (i in 1 until (categories!!.size)){
                listOfCategories.add(categories[i].text())
            }
            handler.post {
                //create buttons
                var newBtn: Button?
                val layout = findViewById<LinearLayout>(R.id.linearCategoryLayout)
                val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                    700, 200
                )
                params.setMargins(0, 20, 0, 0)
                for (i in 0 until listOfCategories.size){
                    newBtn = Button(this)
                    newBtn.text = listOfCategories[i].lowercase()
                    newBtn.setPadding(10)
                    newBtn.setTextColor(Color.parseColor("#ce422b"))
                    newBtn.setBackgroundColor(Color.parseColor("#1b1b1b"))
                    newBtn.textSize = 24.0F
                    newBtn.typeface = Typeface.DEFAULT_BOLD
                    newBtn.layoutParams = params
                    listOfButtons.add(newBtn)
                    layout.addView(newBtn)
                }

                layout.gravity = Gravity.CENTER
                for(btn in listOfButtons){
                    btn.setOnClickListener {
                        viewModel.setChoice(btn.text.toString())
                        navHostFragment!!.navController.navigate(R.id.action_mainFragment_to_categoryFragment)
                    }
                }
            }
        }
    }

    //get all items from under a category
    fun getListOfItems(spinner: Spinner, choice:String){
        listOfItems.clear()
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            val doc: Document = Jsoup.connect("https://www.corrosionhour.com/rust-items-database/").get()
            val elementById = doc.getElementById("content-body")
            var count = -1
            for (i in 1 until elementById!!.childrenSize()){
                count++
                if(elementById.getElementsByTag("h2")[i].text().toString().equals(choice, true)){
                    break
                }
            }
            val get = elementById.getElementsByClass("ch-item-card-wrap")[count]
            var start = 0
            if(choice == "ammunition"){
                start = 1
            }
            for (i in start until get.childrenSize()){
                listOfItems.add(get.getElementsByClass("card")[i].text().toString())
            }
            data = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOfItems)
            data!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            handler.post {
                spinner.adapter = data
            }
        }
    }

    //get one item info
    @SuppressLint("SetTextI18n")
    fun getItemData(choice: String, itemImg: ImageView, itemDescription: TextView,
                    itemID: TextView, craftTitle: TextView, reqWorkLevel: TextView,
                    craftTime: TextView, craftYield: TextView, ingredientsTitle: TextView,
                    ingredientsList:TextView){
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        if(choice != ""){
            executor.execute {
                val doc: Document = Jsoup.connect("https://www.corrosionhour.com/rust-items/${choice}").get()
                val itemInfoTable = doc.getElementsByClass("rust-item-list-common-data item-meta-table")[0]

                //get crafts if available
                val itemCraftTable = doc.getElementsByClass("rust-item-crafting-data item-meta-table")
                val ingredientsTable = doc.getElementsByClass("rust-item-blueprint-data item-meta-table")

                var table = itemInfoTable.select("table")[0]
                var rows = table.select("tr")

                handler.post {
                    //item image
                    val attr = doc.getElementsByClass("featured-image")[0].select("img").attr("src")
                    Picasso.get().load(attr).into(itemImg)

                    //item info
                    for (i in 1 until rows.size){
                        val row = rows[i]
                        val col = row.select("td")
                        if(col[0].text().equals("ItemID")){
                            itemID.text = "Item ID: ${col[1].text()}"
                        }
                        else if(col[0].text().equals("Item Description")){
                            itemDescription.text = "Item Description: ${col[1].text()}"
                        }
                    }
                    //item craft
                    if(itemCraftTable.isNotEmpty()){
                        craftTitle.text = "Crafting Info"
                        table = itemCraftTable[0].select("table")[0]
                        rows = table.select("tr")
                        for (i in 1 until rows.size){
                            val row = rows[i]
                            val col = row.select("td")
                            when {
                                col[0].text().equals("Required Workbench Level") -> {
                                    reqWorkLevel.text = "Required Workbench Level: ${col[1].text()}"
                                }
                                col[0].text().equals("Crafting Time") -> {
                                    craftTime.text = "Crafting Time: ${col[1].text()}"
                                }
                                col[0].text().equals("Crafting Yield") -> {
                                    craftYield.text = "Crafting Yield: ${col[1].text()}"
                                }
                            }
                        }
                    }
                    //item craft
                    if(ingredientsTable.isNotEmpty()){
                        ingredientsTitle.text = "Crafting Ingredients"
                        var list = ""
                        table = ingredientsTable[0].select("table")[0]
                        rows = table.select("tr")
                        for (i in 1 until rows.size){
                            val row = rows[i]
                            val col = row.select("td")
                            list += "${col[1].text()}: ${col[2].text()}\n"
                        }
                        ingredientsList.text = list
                    }
                }
            }
        }

    }
}