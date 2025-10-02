package com.example.ai.ui.admin

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ai.R
import com.example.ai.data.SynonymRepository
import com.example.ai.risk.Detector
import org.json.JSONObject

class DictionaryActivity : ComponentActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var fab: View

    private var data: MutableMap<String, MutableList<String>> = linkedMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary)

        recycler = findViewById(R.id.recycler)
        fab = findViewById(R.id.fab)

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = DictionaryAdapter()

        loadFromStore()

        fab.setOnClickListener {
            promptText(title = getString(R.string.add_canonical_title)) { newCanonical ->
                val word = newCanonical.trim()
                if (word.isEmpty()) {
                    Toast.makeText(this, "空文字は登録できません", Toast.LENGTH_SHORT).show()
                    return@promptText
                }
                if (data.containsKey(word)) {
                    Toast.makeText(this, "すでに登録されています", Toast.LENGTH_SHORT).show()
                    return@promptText
                }
                data[word] = mutableListOf()
                saveToStore()
            }
        }
    }

    private fun loadFromStore() {
        val loaded = SynonymRepository.load(this)
        data = loaded.mapValues { (_, v) -> v.toMutableList() }.toMutableMap(LinkedHashMap())
        recycler.adapter?.notifyDataSetChanged()
    }

    private fun saveToStore() {
        try {
            val immutable = data.mapValues { it.value.toList() }
            SynonymRepository.save(this, immutable)
            Detector.setSynonyms(immutable)
            recycler.adapter?.notifyDataSetChanged()
        } catch (e: Exception) {
            Toast.makeText(this, "保存中にエラーが発生しました", Toast.LENGTH_SHORT).show()
        }
    }

    private fun promptText(title: String, preset: String = "", onDone: (String) -> Unit) {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setText(preset)
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(input)
            .setPositiveButton(android.R.string.ok) { _, _ -> onDone(input.text.toString()) }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    inner class DictionaryAdapter : RecyclerView.Adapter<DictionaryAdapter.VH>() {
        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(R.id.tvCanonical)
            val btnAddSyn: ImageButton = view.findViewById(R.id.btnAddSynonym)
            val list: ViewGroup = view.findViewById(R.id.containerSynonyms)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_canonical, parent, false)
            return VH(v)
        }

        override fun getItemCount(): Int = data.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val canonical = data.keys.elementAt(position)
            val synonyms = data[canonical] ?: mutableListOf()
            holder.title.text = canonical

            holder.list.removeAllViews()
            synonyms.forEachIndexed { index, word ->
                val row = LayoutInflater.from(holder.itemView.context)
                    .inflate(R.layout.item_synonym, holder.list, false)
                val tv: TextView = row.findViewById(R.id.tvSynonym)
                val btnEdit: ImageButton = row.findViewById(R.id.btnEdit)
                val btnDelete: ImageButton = row.findViewById(R.id.btnDelete)
                tv.text = word
                btnEdit.setOnClickListener {
                    promptText(getString(R.string.edit_synonym_title), word) { updatedRaw ->
                        val updated = updatedRaw.trim()
                        if (updated.isEmpty()) {
                            Toast.makeText(this@DictionaryActivity, "空文字は登録できません", Toast.LENGTH_SHORT).show()
                            return@promptText
                        }
                        if (synonyms.any { it.equals(updated, ignoreCase = true) }) {
                            Toast.makeText(this@DictionaryActivity, "すでに登録されています", Toast.LENGTH_SHORT).show()
                            return@promptText
                        }
                        synonyms[index] = updated
                        saveToStore()
                    }
                }
                btnDelete.setOnClickListener {
                    synonyms.removeAt(index)
                    saveToStore()
                }
                holder.list.addView(row)
            }

            holder.btnAddSyn.setOnClickListener {
                promptText(getString(R.string.add_synonym_title)) { newSynRaw ->
                    val newSyn = newSynRaw.trim()
                    if (newSyn.isEmpty()) {
                        Toast.makeText(this@DictionaryActivity, "空文字は登録できません", Toast.LENGTH_SHORT).show()
                        return@promptText
                    }
                    if (synonyms.any { it.equals(newSyn, ignoreCase = true) }) {
                        Toast.makeText(this@DictionaryActivity, "すでに登録されています", Toast.LENGTH_SHORT).show()
                        return@promptText
                    }
                    synonyms += newSyn
                    data[canonical] = synonyms
                    saveToStore()
                }
            }

            holder.itemView.setOnClickListener {
                holder.list.visibility = if (holder.list.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }
        }
    }
}
