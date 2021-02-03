package com.inf3005.android.vocabulario.ui.list

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.inf3005.android.vocabulario.R
import com.inf3005.android.vocabulario.data.Vocabulary
import com.inf3005.android.vocabulario.data.VocabularyAdapter
import com.inf3005.android.vocabulario.databinding.FragmentListBinding
import com.inf3005.android.vocabulario.utilities.SortBy
import com.inf3005.android.vocabulario.utilities.onQueryChanged
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

    private val viewModel: ListViewModel by viewModels()

    override fun onCardClick(entry: Vocabulary) {
        val options = navOptions {
            anim {
                enter = R.anim.slide_in_right
                exit = R.anim.slide_out_left
                popEnter = R.anim.slide_in_left
                popExit = R.anim.slide_out_right
            }
        }

        val action = ListFragmentDirections.actionListFragmentToAddEditFragment(
            entry,
            getString(R.string.edit_entry)
        )
        findNavController().navigate(action, options)
    }

    override fun onTextToSpeechIconClick(entry: Vocabulary) {
        tts.speak(entry.sp, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val binding = FragmentListBinding.bind(view)

        val vocabularyAdapter = VocabularyAdapter(this)

        recyclerView = binding.list

        // Spracheinstellung für die Text-to-Speech-Engine definieren
        val spaLocale = Locale("spa", "MEX")

        binding.apply {
            list.apply {
                adapter = vocabularyAdapter
                layoutManager
                setHasFixedSize(true)
            }

            viewModel.entryCount.observe(viewLifecycleOwner) { entry ->
                binding.emptyListText.isVisible = entry == 0
            }

            /**
             * Text-to-Speech-Engine initialisieren. Dies soll innerhalb einer Coroutine geschehen,
             * um zusätzliche Verzögerungen beim Aufbau des Fragments bestmöglich zu vermeiden.
             *
             * Über die if-Abfrage wird geprüft, ob es Probleme beim Setup des TTS-Objekts gab
             * und sichergestellt, dass zumindest eine TTS-Engine auf dem Gerät installiert und
             * ausgewählt ist.
             * */
            tts = TextToSpeech(requireContext()) { status ->
                GlobalScope.launch {
                    if (status != TextToSpeech.ERROR
                        && tts.defaultEngine.isNotBlank()) {
                        tts.language = spaLocale
                        tts.setSpeechRate(0.9F)
                        tts.setPitch(1.1F)
                    }
                }
            }

            binding.fab.setOnClickListener {
                val options = navOptions {
                    anim {
                        enter = R.anim.slide_in_bottom
                        exit = R.anim.fade_out
                        popEnter = R.anim.slide_in_top
                        popExit = R.anim.fade_out
                    }
                }

                val action = ListFragmentDirections.actionListFragmentToAddEditFragment(
                    null, getString(R.string.add_entry)
                )
                findNavController().navigate(action, options)
            }

            /**
             * ItemTouchHelper für Swipe-to-Delete usw.
             * */
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val entry = vocabularyAdapter.getEntryAt(viewHolder.adapterPosition)

                    // Setze binned = 1 für den Listeneintrag, der gewischt wurde
                    viewModel.updateBinnedState(entry, state = true)

                    /**
                     * Snackbar, die mittels ihrer Action erlaubt binned = 0 für den zugehörigen
                     * Listeneintrag zu setzen, also:
                     *  "Verschieben in den Papierkorb rückgängig machen."
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

                    tts.stop()
                }
            }).attachToRecyclerView(list)

            /**
             * OnScrollListener für die RecyclerView, der verwendet wird, um die Sichtbarkeit
             * des scrollToTop-Button in der ActionBar zu regeln.
             *
             * Wird nach unten gescrollt -> setze die Sichtbarkeit auf true.
             *
             * Die Abfrage für Scrollen nach oben ist komplizierter, da hier berücksichtigt werden
             * muss, ob der Nutzer selbst nach oben scrollt und den Anfang erreicht, oder ob
             * die Liste grundsätzlich am Anfang steht (nicht nach oben scrollbar ist).
             *
             * Letzteres ist wichtig zu beachten, weil über die onSwiped-Funktion des
             * ItemTouchHelper Einträge entfernt werden können, wodurch die Liste selbst "scrollt"
             * - ohne direkte Nutzereingabe.
             * */
            list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) {
                        viewModel.setScrollableState(true)
                        binding.fab.hide()
                    } else if (!list.canScrollVertically(-1)
                    ) {
                        viewModel.setScrollableState(false)
                        binding.fab.show()
                    } else if (dy < 0)
                        binding.fab.show()

                    super.onScrolled(recyclerView, dx, dy)
                }
            })

            /**
             * Observer für den Flow allEntries aus dem ListViewModel.
             * Der Adapter weist die Liste auf Änderungen der Einträge hin - in diesem Fragment nur
             * Verschieben in den Papierkorb (Ändern des 'binned'-value).
             * */
            viewModel.allEntries.observe(viewLifecycleOwner)
            {
                vocabularyAdapter.submitList(it)
            }

            setHasOptionsMenu(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.list_action_bar_menu, menu)

        val searchOption = menu.findItem(R.id.option_search)

        val pendingQuery = viewModel.currentSearchQuery.value

        if (pendingQuery.isNotEmpty()) {
            searchOption.expandActionView()
            searchActionView.setQuery(pendingQuery, false)
        }

        searchActionView = searchOption.actionView as SearchView

        /**
         * Verwendet die in Extensions.kt deklarierte Extension-Inline-Funktion 'onQueryTextChanged'
         * für ein SearchView-Objekt.
         *
         * Nutzereingabe im SearchView-Suchfeld ruft die Extension-Funktion auf, wodurch bei
         * Eingabe/Änderung der Eingabe der Suchstring in den MutableStateFlow 'currentSearchQuery'
         * des ViewModels geschrieben wird.
         * */
        searchActionView.onQueryChanged { query ->
            query.let { viewModel.currentSearchQuery.value = query }
        }
        /**
         * Der first-Operator gibt den ersten Wert des Flows zurück und verwirft ihn anschließend.
         *
         * Somit kann der Wert sortBy, der in der Data Class PreferenceProperties angesprochen
         * werden. Mithilfe dieses Werts kann beim Erzeugen des Optionsmenü derjenige Radio-Button
         * ausgewählt werden, welcher der vom Nutzer ausgewählten und im DataStore gespeicherten
         * Sortierung entspricht.
         * */
        viewLifecycleOwner.lifecycleScope.launch {
            when (viewModel.preferencesFlow.first().sortBy) {
                SortBy.GERMAN -> menu.findItem(R.id.sort_de).isChecked = true
                SortBy.SPANISH -> menu.findItem(R.id.sort_sp).isChecked = true
                SortBy.DIFFICULTY_ASC -> menu.findItem(R.id.sort_difficulty_asc).isChecked = true
                SortBy.DIFFICULTY_DESC -> menu.findItem(R.id.sort_difficulty_desc).isChecked = true
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.scroll_to_top -> {
                recyclerView.layoutManager?.smoothScrollToPosition(
                    recyclerView,
                    null,
                    0
                )
                true
            }
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
//        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
//                || super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        /**
         * Über den Observer der entryCount-LiveData wird die Sichtbarkeit der Suchfunktion
         * geregelt. Sollten die Datenbank keine Einträge mit binned = 0 enthalten, soll die
         * Suchfunktion nicht in der ActionBar angezeigt werden.
         * */
        val searchOption = menu.findItem(R.id.option_search)
        viewModel.entryCount.observe(viewLifecycleOwner) { entry ->
            searchOption.isVisible = entry != 0
        }

        /**
         * In Zusammenarbeit mit OnScrollListener und ItemTouchHelper der RecyclerView regelt
         * dieser Observer der listScrollableState-LiveData die Sichtbarkeit des scrollToTop-
         * Button in der ActionBar.
         * */
        val scrollToTopOption = menu.findItem(R.id.scroll_to_top)
        viewModel.listScrollableState.observe(viewLifecycleOwner) { state ->
            scrollToTopOption.isVisible = state == true
        }
        super.onPrepareOptionsMenu(menu)
    }

    // Wird das Fragment pausiert, soll die Sprachausgabe der TTS-Engine pausiert werden.
    override fun onPause() {
        tts.stop()
        super.onPause()
    }

    override fun onDestroyView() {
        searchActionView.setOnQueryTextListener(null)
        tts.shutdown()
        super.onDestroyView()
    }
}