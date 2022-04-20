package com.example.myapplication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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
    public var listOfItems = ArrayList<String>()
    public var listOfCategories = ArrayList<String>()
    public var listOfButtons = ArrayList<Button>()
    public var data: ArrayAdapter<String>? = null
    var navHostFragment: NavHostFragment? = null



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
                listOfCategories.add(categories.get(i).text())
            }
            handler.post {
                //create buttons
                var newBtn: Button? = null
                var layout = findViewById<LinearLayout>(R.id.linearCategoryLayout)
                for (i in 0 until listOfCategories.size){
                    newBtn = Button(this)
                    newBtn.setLayoutParams(LinearLayout.LayoutParams(700, 200))
                    newBtn.setText(listOfCategories.get(i).toString().lowercase())
                    listOfButtons.add(newBtn)
                    layout.addView(newBtn)
                }
                layout.gravity = Gravity.CENTER
                for(btn in listOfButtons){
                    btn.setOnClickListener(){
                        viewModel!!.setChoice(btn.text.toString())
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
                if(elementById.getElementsByTag("h2").get(i).text().toString().equals(choice, true)){
                    break
                }
            }
            var get = elementById!!.getElementsByClass("ch-item-card-wrap").get(count)
            var start = 0
            if(choice.equals("ammunition")){
                start = 1
            }
            for (i in start until get.childrenSize()){
                listOfItems.add(get.getElementsByClass("card").get(i).text().toString())
            }
            data = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOfItems)
            data!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            handler.post {
                spinner.setAdapter(data)
            }
        }
    }

    //get one item info
    fun getItemData(choice: String, itemImg: ImageView, itemDescription: TextView, itemID: TextView){
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        if(!choice.equals("")){
            executor.execute {
                val doc: Document = Jsoup.connect("https://www.corrosionhour.com/rust-items/${choice}").get()
                val elementById = doc.getElementsByClass("rust-item-list-common-data item-meta-table").get(0)
                val table = elementById.select("table").get(0)
                val rows = table.select("tr")

                handler.post {
                    //item image
                    val attr = doc.getElementsByClass("featured-image").get(0).select("img").attr("src")
                    Picasso.get().load(attr).into(itemImg)

                    //item info
                    for (i in 1 until rows.size){
                        var row = rows.get(i)
                        var col = row.select("td")
                        if(col.get(0).text().equals("ItemID")){
                            itemID.text = "Item ID: ${col.get(1).text()}"
                        }
                        if(col.get(0).text().equals("Item Description")){
                            itemDescription.text = "Item Description: ${col.get(1).text()}"
                        }
                    }
                }
            }
        }

    }
}