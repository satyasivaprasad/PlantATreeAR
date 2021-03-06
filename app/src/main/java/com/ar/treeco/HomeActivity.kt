package com.ar.treeco

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode


class HomeActivity : AppCompatActivity() {

    private var arFragment: ArFragment? = null
    private var andyRenderable: ModelRenderable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        setSupportActionBar(findViewById(R.id.my_toolbar))

        supportActionBar?.title = "Plant a Tree - AR"

        if (intent.hasCategory("tree_co")){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment?

        val adapter = TreeAdapter()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter


        adapter.updateList(getArTreeData())
        adapter.setOnItemClickListener(object : TreeAdapter.OnItemClickListener{
            override fun onItemClick(view: View?, position: Int) {
                val clickedData = adapter.runHistoryList[position]
                createNewModelRender(clickedData.rendable)
            }
        })


        arFragment?.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane, motionEvent: MotionEvent ->
            if (andyRenderable != null) {

                // Create the Anchor.
                val anchor = hitResult.createAnchor()
                val anchorNode = AnchorNode(anchor)
                anchorNode.setParent(arFragment?.arSceneView?.scene)

                // Create the transformable andy and add it to the anchor.
                val andy = TransformableNode(arFragment?.transformationSystem)
                andy.setParent(anchorNode)
                andy.renderable = andyRenderable
                andy.select()
            }

        }

    }

    fun createNewModelRender(modelID: Int) {
        ModelRenderable.builder()
                .setSource(this, modelID)
                .build()
                .thenAccept { renderable -> andyRenderable = renderable
                    Toast.makeText(this, "Tree loaded", Toast.LENGTH_LONG).show()
                }
                .exceptionally { throwable ->
                    Toast.makeText(this, "Unable to load tree renderable", Toast.LENGTH_LONG).show()
                    null
                }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
