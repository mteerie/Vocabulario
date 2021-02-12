package com.inf3005.android.vocabulario.ui.list

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.inf3005.android.vocabulario.R
import com.inf3005.android.vocabulario.data.Vocabulary
import com.inf3005.android.vocabulario.data.VocabularyAdapter
import com.inf3005.android.vocabulario.databinding.FragmentListBinding
import com.inf3005.android.vocabulario.utilities.SortBy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class ListFragment : Fragment(R.layout.fragment_list), VocabularyAdapter.EntryClickListener {

    private lateinit var searchActionView: SearchView

    private lateinit var recyclerView: RecyclerView

    private lateinit var tts: TextToSpeech

    private lateinit var emptyListText: TextView

    private val viewModel: ListViewModel by viewModels()

    // Überschreibe Funktionen des EntryClickListener-Interface.
    override fun onCardClick(entry: Vocabulary) {
        // Übergebe den geklickten entry und den Titel für das AddEditFragment
        val action = ListFragmentDirections.actionListFragmentToAddEditFragment(
            entry,
            getString(R.string.edit_entry)
        )
        findNavController().navigate(action)
        return
    }

    override fun onTextToSpeechIconClick(entry: Vocabulary) {

        // Spreche das ausgewählte Wort aus und speichere den Text im viewModel.
        viewModel.setSpokenText(entry.sp)
        tts.speak(entry.sp, TextToSpeech.QUEUE_FLUSH, null, null)
        return
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val binding = FragmentListBinding.bind(view)

        val vocabularyAdapter = VocabularyAdapter(this)

        // Initialisieren, damit die Variable später auch in onOptionsItemSelected ben. werden kann
        recyclerView = binding.list

        emptyListText = binding.emptyListText

        // Spracheinstellung für die Text-to-Speech-Engine definieren
        val esLocale = Locale("es", "ES")

        // Zu Testzwecken: Bietet andere TTS-Stimme als esLocale.
        // val mexlocale = Locale("es", "MEX")

        binding.apply {
            list.apply {
                adapter = vocabularyAdapter
                setHasFixedSize(true)
            }

            /**
             * Text-to-Speech-Objekt initialisieren. Geschieht innerhalb einer Coroutine, um
             * Verzögerungen beim Aufbau des Fragments bestmöglich zu vermeiden.
             *
             * Funktionalität erfordert, dass eine TTS-Engine auf dem Gerät installiert ist.
             *
             * if-Abfrage prüft, ob es Probleme beim Setup des TTS-Objekts gab.
             * */
            tts = TextToSpeech(requireContext()) { status ->
                lifecycleScope.launch {
                    if (status != TextToSpeech.ERROR) {
                        tts.language = esLocale
                    }
                }
            }

            binding.fab.setOnClickListener {
                // Analog zu onCardClick mit entry = null.
                val action = ListFragmentDirections.actionListFragmentToAddEditFragment(
                    null, getString(R.string.add_entry)
                )
                findNavController().navigate(action)
            }

            // ItemTouchHelper für Swipe-to-Delete usw.
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {

                // Einträge sollen nicht frei bewegt werden können, daher nicht benötigt.
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                // Reagiere auf "Wegwischen" eines Eintrags.
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val entry = vocabularyAdapter.getEntryAt(viewHolder.adapterPosition)

                    // Setze binned = true für den Listeneintrag, der gewischt wurde.
                    viewModel.updateBinnedState(entry, state = true)

                    /**
                     * Snackbar, die als Action anbietet binned für entry wieder auf false
                     * zu setzen. Effektiv: "Verschieben in den Papierkorb rückgängig machen".
                     */
                    Snackbar.make(
                        requireView(),
                        getString(R.string.list_entry_moved_to_bin),
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(getString(R.string.list_entry_undo)) {
                            viewModel.updateBinnedState(entry, state = false)
                        }
                        .setActionTextColor(ContextCompat.getColor(context!!, R.color.black))
                        .setTextColor(ContextCompat.getColor(context!!, R.color.black))
                        .setAnchorView(binding.snackbarAnchor)
                        .show()

                    /**
                     * Prüfe ob die TTS-Engine spricht und ob das gesprochene Wort dem Eintrag
                     * entspricht, der in den Papierkorb verschoben wurde.
                     *
                     * Falls ja: stoppe die Sprachausgabe.
                     * */
                    if (tts.isSpeaking && viewModel.getLastSpokenText() == entry.sp)
                        tts.stop()
                }
            }).attachToRecyclerView(list)

            /**
             * OnScrollListener für die RecyclerView, der verwendet wird, um die Sichtbarkeit
             * des Scroll-to-Top-Button in der ActionBar und des FAB zu regeln.
             * */
            list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) {
                        viewModel.setScrollToTopVisible(true)
                        binding.fab.hide()
                    } else if (!list.canScrollVertically(-1)) {
                        viewModel.setScrollToTopVisible(false)
                        binding.fab.show()
                    } else if (dy < 0)
                        binding.fab.show()
                    else if (list.canScrollVertically(-1))
                        viewModel.setScrollToTopVisible(true)

                    super.onScrolled(recyclerView, dx, dy)
                }
            })

            /**
             * Observer für den Flow allEntries aus dem ListViewModel.
             *
             * Der Adapter weist die Liste auf Änderungen der Einträge hin.
             *
             * Beispiel: Nutzer ändert die Sortierung der Liste.
             * */
            viewModel.allEntries.observe(viewLifecycleOwner)
            {
                vocabularyAdapter.submitList(it)
            }

            setHasOptionsMenu(true)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        /**
         * Sichtbarkeit der Suchfunktion in der ActionBar und des EmptyListText wird über diesen
         * Observer geregelt.
         *
         * EmptyListText nur anzeigen, wenn die Liste leer ist.
         *
         * Suchen-Button nur anzeigen, wenn mind. ein Eintrag in der liste ist.
         * */
        val searchOption = menu.findItem(R.id.option_search)
        viewModel.entryCount.observe(viewLifecycleOwner) { count ->
            searchOption.isVisible = count > 0
            emptyListText.isVisible = count == 0
        }

        /**
         * Sichtbarkeit des Scroll-to-Top-Button wird über diesen Observer abhängig von
         * scrollToTopVisible in viewModel geregelt.
         * */
        val scrollToTopOption = menu.findItem(R.id.scroll_to_top)
        viewModel.scrollToTopVisible.observe(viewLifecycleOwner) { state ->
            scrollToTopOption.isVisible = state
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_action_bar_menu, menu)

        val searchOption = menu.findItem(R.id.option_search)
        searchActionView = searchOption.actionView as SearchView

        // Speichere die derzeitige Sucheingabe aus dem ViewModel.
        val stateQuery = viewModel.getSearchQuery()

        // Ist die Sucheingabe nicht null oder leer, öffne Suchfeld und fülle es mit Eingabe.
        if (!stateQuery.isNullOrEmpty()) {
            searchOption.expandActionView()
            searchActionView.setQuery(stateQuery, false)
        }

        // Reagiert auf Eingaben im Suchfeld und setzt viewModel.currentSearchQuery entsprechend.
        searchActionView
            .setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.setSearchQuery(newText)
                    return true
                }
            })

        /**
         * Checke den RadioButton, der jener Sortieroption entspricht, die der Nutzer ausgewählt
         * hat (in DataStorePreferences - PreferenceProperties hinterlegt).
         * */
        viewLifecycleOwner.lifecycleScope.launch {
            when (viewModel.userPrefFlow.first().sortBy) {
                SortBy.GERMAN -> menu.findItem(R.id.sort_de).isChecked = true
                SortBy.SPANISH -> menu.findItem(R.id.sort_sp).isChecked = true
                SortBy.DIFFICULTY_ASC -> menu.findItem(R.id.sort_difficulty_asc).isChecked = true
                SortBy.DIFFICULTY_DESC -> menu.findItem(R.id.sort_difficulty_desc).isChecked = true
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            // Scroll-to-Top-Button scrollt die Liste geschmeidig zu ihrem Anfang.
            R.id.scroll_to_top -> {
                recyclerView.layoutManager?.smoothScrollToPosition(
                    recyclerView,
                    null,
                    0
                )
                true
            }

            // Block für Sortierungsoptionen. Ruft viewModel-Funktion mit entspr. Sortierung auf.
            R.id.sort_de -> {
                viewModel.onSortOptionSelected(SortBy.GERMAN)
                item.isChecked = true
                true
            }
            R.id.sort_sp -> {
                viewModel.onSortOptionSelected(SortBy.SPANISH)
                item.isChecked = true
                true
            }
            R.id.sort_difficulty_asc -> {
                viewModel.onSortOptionSelected(SortBy.DIFFICULTY_ASC)
                item.isChecked = true
                true
            }
            R.id.sort_difficulty_desc -> {
                viewModel.onSortOptionSelected(SortBy.DIFFICULTY_DESC)
                item.isChecked = true
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Stoppe etwaige Sprachausgabe der TTS-Engine, wenn der Nutzer bspw. den Home-Button drückt.
    override fun onPause() {
        tts.stop()
        super.onPause()
    }

    // Schalte die TTS-Engine ab und entferne den OnQueryTextListener von searchActionView.
    override fun onDestroyView() {
        tts.shutdown()
        searchActionView.setOnQueryTextListener(null)
        super.onDestroyView()
    }
}