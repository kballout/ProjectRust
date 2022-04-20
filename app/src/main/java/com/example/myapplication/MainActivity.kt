package com.example.myapplication

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.myapplication.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    public var listOfItems = ArrayList<String>()
    public var data: ArrayAdapter<String>? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        setupActionBarWithNavController(navHostFragment.navController)

//        getSite()
    }

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
                if(elementById.getElementsByTag("h2").get(i).text().equals(choice)){
                    break
                }
            }
            var get = elementById!!.getElementsByClass("ch-item-card-wrap").get(count)
            var start = 0
            if(choice.equals("Ammunition")){
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

    fun getItemData(choice: String, itemImg: ImageView, itemDescription: TextView){
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
                        if(col.get(0).text().equals("Item Description")){
                            itemDescription.text = col.get(1).text()
                        }
                    }
                }
            }
        }

    }

    fun getSite(){
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            val doc: Document = Jsoup.connect("https://www.corrosionhour.com/rust-items-database/").get()
            val elementById = doc.getElementById("content-body")
            var ch = "Ammunition"
            var count = -1
            for (i in 1 until elementById!!.childrenSize()){
                count++
                if(elementById.getElementsByTag("h2").get(i).text().equals(ch)){
                    break
                }
            }
            println(count)
            var get = elementById!!.getElementsByClass("ch-item-card-wrap").get(count)
            println(get.text())

            handler.post {
            }
        }


    }
}