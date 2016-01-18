package app.com.augmentedrealitytraining.training.ar;

/**
 * Created by Abhijit on 10/18/2015.
 */

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.Display;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import app.com.augmentedrealitytraining.R;

import static app.com.augmentedrealitytraining.training.ar.DataUtils.BACKUP_FILE_NAME;
import static app.com.augmentedrealitytraining.training.ar.DataUtils.BACKUP_FOLDER_PATH;
import static app.com.augmentedrealitytraining.training.ar.DataUtils.NEW_NOTE_REQUEST;
import static app.com.augmentedrealitytraining.training.ar.DataUtils.NOTES_FILE_NAME;
import static app.com.augmentedrealitytraining.training.ar.DataUtils.NOTE_BODY;
import static app.com.augmentedrealitytraining.training.ar.DataUtils.NOTE_COLOUR;
import static app.com.augmentedrealitytraining.training.ar.DataUtils.NOTE_FAVOURED;
import static app.com.augmentedrealitytraining.training.ar.DataUtils.NOTE_FONT_SIZE;
import static app.com.augmentedrealitytraining.training.ar.DataUtils.NOTE_HIDE_BODY;
import static app.com.augmentedrealitytraining.training.ar.DataUtils.NOTE_REQUEST_CODE;
import static app.com.augmentedrealitytraining.training.ar.DataUtils.NOTE_TITLE;
import static app.com.augmentedrealitytraining.training.ar.DataUtils.deleteNotes;
import static app.com.augmentedrealitytraining.training.ar.DataUtils.isExternalStorageReadable;
import static app.com.augmentedrealitytraining.training.ar.DataUtils.isExternalStorageWritable;
import static app.com.augmentedrealitytraining.training.ar.DataUtils.retrieveData;
import static app.com.augmentedrealitytraining.training.ar.DataUtils.saveData;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Notes extends Fragment implements AbsListView.MultiChoiceModeListener,
        Toolbar.OnMenuItemClickListener, SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {
    public static boolean inNote;
    private static File localPath, backupPath;

    // Layout components
    private static ListView listView;
    private ImageButton newNote;
    private TextView noNotes;
    private Toolbar toolbar;
    private MenuItem searchMenu;

    private static JSONArray notes; // Main notes array
    private static NoteAdapter adapter; // Custom ListView notes adapter

    // Array of selected positions for deletion
    public static ArrayList<Integer> checkedArray = new ArrayList<Integer>();
    public static boolean deleteActive = false; // True if delete mode is active, false otherwise

    // For disabling long clicks, favourite clicks and modifying the item click pattern
    public static boolean searchActive = false;
    private ArrayList<Integer> realIndexesOfSearchResults; // To keep track of real indexes in searched notes

    private int lastFirstVisibleItem = -1; // Last first item seen in list view scroll changed
    private float newNoteButtonBaseYCoordinate; // Base Y coordinate of newNote button

    private AlertDialog backupCheckDialog, backupOKDialog, restoreCheckDialog, restoreFailedDialog;
    //private EditText  bodyEdit;
    private Bundle bundle;




    Notes(Intent intent) {
        super();
        bundle = intent.getExtras();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //getTargetFragment().getView().setBackgroundColor(Color.black);
        final View view = inflater.inflate(R.layout.activity_note, container, false);


        //DrawerItemClickListener.popupWindow(view, false);
        //toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        //set toolbar appearance
        //toolbar.setBackground(R.color.);

        //for crate home button
        //AppCompatActivity activity = (AppCompatActivity) getActivity();
        //activity.setSupportActionBar(toolbar);
        //activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return view;
        //return inflater.inflate(R.layout.activity_note, container, false );

    }

    //Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //final ActionBar action = getActionBar();
//        getActivity().getActionBar().setTitle("Notes");
//        getActivity().getActionBar().setIcon(null);
//        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);
//        toolbar.setBackgroundColor(Color.CYAN);

//        getActivity().getActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
//                .getColor(R.color.drop_shadow_grey)));
        // setHasOptionsMenu(true);
        //setRetainInstance(true);
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        menu.clear();
//        inflater.inflate(R.menu.menu_note, menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if(id == R.id.action_search ){
//            //Do whatever you want to do
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onStart() {
        super.onStart();

        // Initialize local file path and backup file path
        localPath = new File(getActivity().getFilesDir() + "/" + NOTES_FILE_NAME);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        File backupFolder = new File(Environment.getExternalStorageDirectory() +
                BACKUP_FOLDER_PATH);

        if (isExternalStorageReadable() && isExternalStorageWritable() && !backupFolder.exists()) {
            backupFolder.mkdir();
        }

        backupPath = new File(backupFolder, BACKUP_FILE_NAME);

        // Android version < 18 -> set orientation sensorPortrait
        if (Build.VERSION.SDK_INT >= 18) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
        // Init notes array
        notes = new JSONArray();

        // Retrieve from local path
        JSONArray tempNotes = retrieveData(localPath);

        // If not null -> equal main notes to retrieved notes
        if (tempNotes != null) {
            notes = tempNotes;
        }

        // Init layout components

        toolbar = (Toolbar) getView().findViewById(R.id.toolbarMain);
        listView = (ListView) getView().findViewById(R.id.listView);
        newNote = (ImageButton) getView().findViewById(R.id.newNote);
        noNotes = (TextView) getView().findViewById(R.id.noNotes);

        if (toolbar != null)
            initToolbar();

        newNoteButtonBaseYCoordinate = newNote.getY();

        // Initialize NoteAdapter with notes array
        adapter = new NoteAdapter(getActivity().getApplicationContext(), notes);
        listView.setAdapter(adapter);

        // Set item click, multi choice and scroll listeners
        listView.setOnItemClickListener(this);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(this);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // If last first visible item not initialized -> set to current first
                if (lastFirstVisibleItem == -1) {
                    lastFirstVisibleItem = view.getFirstVisiblePosition();
                }

                // If scrolled up -> hide newNote button
                if (view.getFirstVisiblePosition() > lastFirstVisibleItem) {
                    newNoteButtonVisibility(false);
                }

                // If scrolled down and delete/search not active -> show newNote button
                else if (view.getFirstVisiblePosition() < lastFirstVisibleItem &&
                        !deleteActive && !searchActive) {

                    newNoteButtonVisibility(true);
                }

                // Set last first visible item to current
                lastFirstVisibleItem = view.getFirstVisiblePosition();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
            }
        });


        newNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra(NOTE_REQUEST_CODE, NEW_NOTE_REQUEST);
                inNote = true;
                EditActivity fragment1 = new EditActivity(intent, Notes.this, NEW_NOTE_REQUEST);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.edit, fragment1);
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                getActivity().findViewById(R.id.edit).getLayoutParams().height = size.y / 2;
                getActivity().findViewById(R.id.edit).getLayoutParams().width = size.x / 2;
                fragmentTransaction.commit();

            }
        });




    // If newNote button clicked -> Start EditActivity intent with NEW_NOTE_REQUEST as request

    // If no notes -> show 'Press + to add new note' text, invisible otherwise
        if (notes.length() == 0) {
            noNotes.setVisibility(View.VISIBLE);
        } else {
            noNotes.setVisibility(View.INVISIBLE);
        }

        initDialogs(getActivity());
    }


    /**
     * Initialize toolbar with required components such as
     * - title, menu/OnMenuItemClickListener and searchView -
     */
    protected void initToolbar() {
        //getActivity().setActionBar(toolbar);
        toolbar.setTitle("Notes");

        // Inflate menu_note to be displayed in the toolbar
        toolbar.inflateMenu(R.menu.menu_note);

        // Set an OnMenuItemClickListener to handle menu item clicks
        toolbar.setOnMenuItemClickListener(this);

        Menu menu = toolbar.getMenu();

        if (menu != null) {
            // Get 'Search' menu item
            searchMenu = menu.findItem(R.id.action_search);

            if (searchMenu != null) {
                // If the item menu not null -> get it's support action view
                SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenu);

                if (searchView != null) {
                    // If searchView not null -> set query hint and open/query/close listeners
                    searchView.setQueryHint(getString(R.string.action_search));
                    searchView.setOnQueryTextListener(this);

                    MenuItemCompat.setOnActionExpandListener(searchMenu,
                            new MenuItemCompat.OnActionExpandListener() {

                                @Override
                                public boolean onMenuItemActionExpand(MenuItem item) {
                                    searchActive = true;
                                    newNoteButtonVisibility(false);
                                    // Disable long-click on listView to prevent deletion
                                    listView.setLongClickable(false);

                                    // Init realIndexes array
                                    realIndexesOfSearchResults = new ArrayList<>();
                                    for (int i = 0; i < notes.length(); i++)
                                        realIndexesOfSearchResults.add(i);

                                    adapter.notifyDataSetChanged();

                                    return true;
                                }

                                @Override
                                public boolean onMenuItemActionCollapse(MenuItem item) {
                                    searchEnded();
                                    return true;
                                }
                            });
                }
            }
        }
    }


    /**
     * Implementation of AlertDialogs such as
     * - backupCheckDialog, backupOKDialog, restoreCheckDialog, restoreFailedDialog -
     *
     * @param context The Activity context of the dialogs; in this case MainActivity context
     */
    protected void initDialogs(Context context) {
        /*
         * Backup check dialog
         *  If not sure -> dismiss
         *  If yes -> check if notes length > 0
         *    If yes -> save current notes to backup file in backupPath
         */
        backupCheckDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.action_backup)
                .setMessage(R.string.dialog_check_backup_if_sure)
                .setPositiveButton(R.string.yes_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // If note array not empty -> continue
                        if (notes.length() > 0) {
                            boolean backupSuccessful = saveData(backupPath, notes);

                            if (backupSuccessful)
                                showBackupSuccessfulDialog();

                            else {
                                Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                                        getResources().getString(R.string.toast_backup_failed),
                                        Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }

                        // If notes array is empty -> toast backup no notes found
                        else {
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                                    getResources().getString(R.string.toast_backup_no_notes),
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                })
                .setNegativeButton(R.string.no_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();


        // Dialog to display backup was successfully created in backupPath
        backupOKDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.dialog_backup_created_title)
                .setMessage(getString(R.string.dialog_backup_created) + " "
                        + backupPath.getAbsolutePath())
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();


        /*
         * Restore check dialog
         *  If not sure -> dismiss
         *  If yes -> check if backup notes exists
         *    If not -> display restore failed dialog
         *    If yes -> retrieve notes from backup file and store into local file
         */
        restoreCheckDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.action_restore)
                .setMessage(R.string.dialog_check_restore_if_sure)
                .setPositiveButton(R.string.yes_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JSONArray tempNotes = retrieveData(backupPath);

                        // If backup file exists -> copy backup notes to local file
                        if (tempNotes != null) {
                            boolean restoreSuccessful = saveData(localPath, tempNotes);

                            if (restoreSuccessful) {
                                notes = tempNotes;

                                adapter = new NoteAdapter(getActivity().getApplicationContext(), notes);
                                listView.setAdapter(adapter);

                                Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                                        getResources().getString(R.string.toast_restore_successful),
                                        Toast.LENGTH_SHORT);
                                toast.show();

                                // If no notes -> show 'Press + to add new note' text, invisible otherwise
                                if (notes.length() == 0)
                                    noNotes.setVisibility(View.VISIBLE);

                                else
                                    noNotes.setVisibility(View.INVISIBLE);
                            }

                            // If restore unsuccessful -> toast restore unsuccessful
                            else {
                                Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                                        getResources().getString(R.string.toast_restore_unsuccessful),
                                        Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }

                        // If backup file doesn't exist -> show restore failed dialog
                        else
                            showRestoreFailedDialog();
                    }
                })
                .setNegativeButton(R.string.no_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();


        // Dialog to display restore failed when no backup file found
        restoreFailedDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.dialog_restore_failed_title)
                .setMessage(R.string.dialog_restore_failed)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    // Method to dismiss backup check and show backup successful dialog
    protected void showBackupSuccessfulDialog() {
        backupCheckDialog.dismiss();
        backupOKDialog.show();
    }

    // Method to dismiss restore check and show restore failed dialog
    protected void showRestoreFailedDialog() {
        restoreCheckDialog.dismiss();
        restoreFailedDialog.show();
    }


    /**
     * If item clicked in list view -> Start EditActivity intent with position as requestCode
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        inNote = true;
        // If search is active -> use position from realIndexesOfSearchResults for EditActivity
        if (searchActive) {
            int newPosition = realIndexesOfSearchResults.get(position);

            try {
                // Package selected note content and send to EditActivity
                intent.putExtra(NOTE_TITLE, notes.getJSONObject(newPosition).getString(NOTE_TITLE));
                intent.putExtra(NOTE_BODY, notes.getJSONObject(newPosition).getString(NOTE_BODY));
                intent.putExtra(NOTE_COLOUR, notes.getJSONObject(newPosition).getString(NOTE_COLOUR));
                intent.putExtra(NOTE_FONT_SIZE, notes.getJSONObject(newPosition).getInt(NOTE_FONT_SIZE));

                if (notes.getJSONObject(newPosition).has(NOTE_HIDE_BODY)) {
                    intent.putExtra(NOTE_HIDE_BODY,
                            notes.getJSONObject(newPosition).getBoolean(NOTE_HIDE_BODY));
                } else
                    intent.putExtra(NOTE_HIDE_BODY, false);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            intent.putExtra(NOTE_REQUEST_CODE, newPosition);


            EditActivity fragment1 = new EditActivity(intent, Notes.this, newPosition);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.edit, fragment1);
            //getActivity().findViewById(R.id.edit).setPadding(100, 100, 100, 100);
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            getActivity().findViewById(R.id.edit).getLayoutParams().height = size.y/2;
            getActivity().findViewById(R.id.edit).getLayoutParams().width = size.x/2;
            fragmentTransaction.commit();
        }

        // If search is not active -> use normal position for EditActivity
        else {
            try {
                // Package selected note content and send to EditActivity
                intent.putExtra(NOTE_TITLE, notes.getJSONObject(position).getString(NOTE_TITLE));
                intent.putExtra(NOTE_BODY, notes.getJSONObject(position).getString(NOTE_BODY));
                intent.putExtra(NOTE_COLOUR, notes.getJSONObject(position).getString(NOTE_COLOUR));
                intent.putExtra(NOTE_FONT_SIZE, notes.getJSONObject(position).getInt(NOTE_FONT_SIZE));

                if (notes.getJSONObject(position).has(NOTE_HIDE_BODY)) {
                    intent.putExtra(NOTE_HIDE_BODY,
                            notes.getJSONObject(position).getBoolean(NOTE_HIDE_BODY));
                } else
                    intent.putExtra(NOTE_HIDE_BODY, false);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            intent.putExtra(NOTE_REQUEST_CODE, position);
            EditActivity fragment1 = new EditActivity(intent, Notes.this, position);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.edit, fragment1);
            //getActivity().findViewById(R.id.edit).setPadding(100, 100, 100, 100);
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            getActivity().findViewById(R.id.edit).getLayoutParams().height = size.y/2;
            getActivity().findViewById(R.id.edit).getLayoutParams().width = size.x/2;
            fragmentTransaction.commit();
        }
    }

    /**
     * Item clicked in Toolbar menu callback method
     *
     * @param menuItem Item clicked
     * @return true if click detected and logic finished, false otherwise
     */
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        int id = menuItem.getItemId();

        // 'Backup notes' pressed -> show backupCheckDialog
        if (id == R.id.action_backup) {
            backupCheckDialog.show();
            return true;
        }

        // 'Restore notes' pressed -> show restoreCheckDialog
        if (id == R.id.action_restore) {
            restoreCheckDialog.show();
            return true;
        }

        return false;
    }


    /**
     * During multi-choice menu_delete selection mode, callback method if items checked changed
     *
     * @param mode     ActionMode of selection
     * @param position Position checked
     * @param id       ID of item, if exists
     * @param checked  true if checked, false otherwise
     */
    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        // If item checked -> add to array
        if (checked)
            checkedArray.add(position);

            // If item unchecked
        else {
            int index = -1;

            // Loop through array and find index of item unchecked
            for (int i = 0; i < checkedArray.size(); i++) {
                if (position == checkedArray.get(i)) {
                    index = i;
                    break;
                }
            }

            // If index was found -> remove the item
            if (index != -1)
                checkedArray.remove(index);
        }

        // Set Toolbar title to 'x Selected'

        mode.setTitle(checkedArray.size() + " " + getString(R.string.action_delete_selected_number));

        adapter.notifyDataSetChanged();
    }

    /**
     * Callback method when 'Delete' icon pressed
     *
     * @param mode ActionMode of selection
     * @param item MenuItem clicked, in our case just action_delete
     * @return true if clicked, false otherwise
     */
    @Override
    public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.dialog_delete)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Pass notes and checked items for deletion array to 'deleteNotes'
                            notes = deleteNotes(notes, checkedArray);

                            // Create and set new adapter with new notes array
                            adapter = new NoteAdapter(getActivity().getApplicationContext(), notes);
                            listView.setAdapter(adapter);

                            // Attempt to save notes to local file
                            Boolean saveSuccessful = saveData(localPath, notes);

                            // If save successful -> toast successfully deleted
                            if (saveSuccessful) {
                                Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                                        getResources().getString(R.string.toast_deleted),
                                        Toast.LENGTH_SHORT);
                                toast.show();
                            }

                            // Smooth scroll to top
                            listView.post(new Runnable() {
                                public void run() {
                                    listView.smoothScrollToPosition(0);
                                }
                            });

                            // If no notes -> show 'Press + to add new note' text, invisible otherwise
                            if (notes.length() == 0)
                                noNotes.setVisibility(View.VISIBLE);

                            else
                                noNotes.setVisibility(View.INVISIBLE);

                            mode.finish();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();

            return true;
        }

        if (item.getItemId() == R.id.share_notes) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String s = "";
            boolean flag = false;
            for (int i = 0; i < notes.length(); i++) {
                // If array of positions to delete doesn't contain current position -> put in new array
                if (checkedArray.contains(i)) {
                    try {
                        if (flag) {
                            s += "\n\n";
                        }
                        s += "Title : " + notes.getJSONObject(i).getString(NOTE_TITLE) + "\n" + "Content : "
                                + notes.getJSONObject(i).getString(NOTE_BODY);
                        flag = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, s);
            startActivity(Intent.createChooser(sharingIntent, "Share Notes").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        }
        return false;
    }


    // Long click detected on ListView item -> start selection ActionMode (delete mode)
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_delete, menu); // Inflate 'menu_delete' menu
        deleteActive = true; // Set deleteActive to true as we entered delete mode
        newNoteButtonVisibility(false); // Hide newNote button
        adapter.notifyDataSetChanged(); // Notify adapter to hide favourite buttons

        return true;
    }

    //Selection ActionMode finished (delete mode ended)
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        checkedArray = new ArrayList<>(); // Reset checkedArray
        deleteActive = false; // Set deleteActive to false as we finished delete mode
        newNoteButtonVisibility(true); // Show newNote button
        adapter.notifyDataSetChanged(); // Notify adapter to show favourite buttons
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }


    /**
     * Method to show and hide the newNote button
     *
     * @param isVisible true to show button, false to hide
     */
    protected void newNoteButtonVisibility(boolean isVisible) {
        if (isVisible) {
            newNote.animate().cancel();
            newNote.animate().translationY(newNoteButtonBaseYCoordinate);
        } else {
            newNote.animate().cancel();
            newNote.animate().translationY(newNoteButtonBaseYCoordinate + 500);
        }
    }


    /**
     * Callback method for 'searchView' menu item widget text change
     *
     * @param s String which changed
     * @return true if text changed and logic finished, false otherwise
     */
    @Override
    public boolean onQueryTextChange(String s) {
        s = s.toLowerCase(); // Turn string into lowercase

        // If query text length longer than 0
        if (s.length() > 0) {
            // Create new JSONArray and reset realIndexes array
            JSONArray notesFound = new JSONArray();
            realIndexesOfSearchResults = new ArrayList<>();

            // Loop through main notes list
            for (int i = 0; i < notes.length(); i++) {
                JSONObject note = null;

                // Get note at position i
                try {
                    note = notes.getJSONObject(i);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // If note not null and title/body contain query text
                // -> Put in new notes array and add i to realIndexes array
                if (note != null) {
                    try {
                        if (note.getString(NOTE_TITLE).toLowerCase().contains(s) ||
                                note.getString(NOTE_BODY).toLowerCase().contains(s)) {

                            notesFound.put(note);
                            realIndexesOfSearchResults.add(i);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Create and set adapter with notesFound to refresh ListView
            NoteAdapter searchAdapter = new NoteAdapter(getActivity().getApplicationContext(), notesFound);
            listView.setAdapter(searchAdapter);
        }

        // If query text length is 0 -> re-init realIndexes array (0 to length) and reset adapter
        else {
            realIndexesOfSearchResults = new ArrayList<>();
            for (int i = 0; i < notes.length(); i++)
                realIndexesOfSearchResults.add(i);

            adapter = new NoteAdapter(getActivity().getApplicationContext(), notes);
            listView.setAdapter(adapter);
        }

        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }


    /**
     * When search mode is finished
     * Collapse searchView widget, searchActive to false, reset adapter, enable listView long clicks
     * and show newNote button
     */
    protected void searchEnded() {
        searchActive = false;
        adapter = new NoteAdapter(getActivity().getApplicationContext(), notes);
        listView.setAdapter(adapter);
        listView.setLongClickable(true);
        newNoteButtonVisibility(true);
    }


    /**
     * Callback method when EditActivity finished adding new note or editing existing note
     *
     * @param requestCode requestCode for intent sent, in our case either NEW_NOTE_REQUEST or position
     * @param resultCode  resultCode from activity, either RESULT_OK or RESULT_CANCELED
     * @param data        Data bundle passed back from EditActivity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            // If search was active -> call 'searchEnded' method
            if (searchActive && searchMenu != null)
                searchMenu.collapseActionView();

            // Get extras
            Bundle mBundle = null;
            if (data != null)
                mBundle = data.getExtras();

            if (mBundle != null) {
                // If new note was saved
                if (requestCode == NEW_NOTE_REQUEST) {
                    JSONObject newNoteObject = null;

                    try {
                        // Add new note to array
                        newNoteObject = getNoteJSONObject(mBundle);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // If newNoteObject not null -> save notes array to local file and notify adapter
                    if (newNoteObject != null) {
                        adapter.notifyDataSetChanged();

                        Boolean saveSuccessful = saveData(localPath, notes);

                        if (saveSuccessful) {
                            Toast toast = Toast.makeText(ARApplication.getInstance().getApplicationContext(),
                                    ARApplication.getInstance().getResources().getString(R.string.toast_new_note),
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        }

                        // If no notes -> show 'Press + to add new note' text, invisible otherwise
                        if (notes.length() == 0)
                            noNotes.setVisibility(View.VISIBLE);

                        else
                            noNotes.setVisibility(View.INVISIBLE);
                    }
                }

                // If existing note was updated (saved)
                else {
                    JSONObject newNoteObject = null;

                    try {
                        // Update array item with new note data
                        newNoteObject = notes.getJSONObject(requestCode);
                        newNoteObject.put(NOTE_TITLE, mBundle.getString(NOTE_TITLE));
                        newNoteObject.put(NOTE_BODY, mBundle.getString(NOTE_BODY));
                        newNoteObject.put(NOTE_COLOUR, mBundle.getString(NOTE_COLOUR));
                        newNoteObject.put(NOTE_FONT_SIZE, mBundle.getInt(NOTE_FONT_SIZE));
                        newNoteObject.put(NOTE_HIDE_BODY, mBundle.getBoolean(NOTE_HIDE_BODY));

                        // Update note at position 'requestCode'
                        notes.put(requestCode, newNoteObject);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // If newNoteObject not null -> save notes array to local file and notify adapter
                    if (newNoteObject != null) {
                        adapter.notifyDataSetChanged();

                        Boolean saveSuccessful = saveData(localPath, notes);

                        if (saveSuccessful) {
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                                    getResources().getString(R.string.toast_note_saved),
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }
            }
        } else if (resultCode == getActivity().RESULT_CANCELED) {
            Bundle mBundle;

            // If data is not null, has "request" extra and is new note -> get extras to bundle
            if (data != null && data.hasExtra("request") && requestCode == NEW_NOTE_REQUEST) {
                mBundle = data.getExtras();

                // If new note discarded -> toast empty note discarded
                if (mBundle != null && mBundle.getString("request").equals("discard")) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                            getResources().getString(R.string.toast_empty_note_discarded),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @NonNull
    private JSONObject getNoteJSONObject(Bundle mBundle) throws JSONException {
        JSONObject newNoteObject;
        newNoteObject = new JSONObject();
        newNoteObject.put(NOTE_TITLE, mBundle.getString(NOTE_TITLE));
        newNoteObject.put(NOTE_BODY, mBundle.getString(NOTE_BODY));
        newNoteObject.put(NOTE_COLOUR, mBundle.getString(NOTE_COLOUR));
        newNoteObject.put(NOTE_FAVOURED, false);
        newNoteObject.put(NOTE_FONT_SIZE, mBundle.getInt(NOTE_FONT_SIZE));
        newNoteObject.put(NOTE_HIDE_BODY, mBundle.getBoolean(NOTE_HIDE_BODY));

        notes.put(newNoteObject);
        return newNoteObject;
    }


    /**
     * Favourite or un-favourite the note at position
     *
     * @param context   application context
     * @param favourite true to favourite, false to un-favourite
     * @param position  position of note
     */
    public static void setFavourite(Context context, boolean favourite, int position) {
        JSONObject newFavourite = null;

        // Get note at position and store in newFavourite
        try {
            newFavourite = notes.getJSONObject(position);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (newFavourite != null) {
            if (favourite) {
                // Set favoured to true
                try {
                    newFavourite.put(NOTE_FAVOURED, true);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // If favoured note is not at position 0
                // Sort notes array so favoured note is first
                if (position > 0) {
                    JSONArray newArray = new JSONArray();

                    try {
                        newArray.put(0, newFavourite);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Copy contents to new sorted array without favoured element
                    for (int i = 0; i < notes.length(); i++) {
                        if (i != position) {
                            try {
                                newArray.put(notes.get(i));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    // Equal main notes array with new sorted array and reset adapter
                    notes = newArray;
                    adapter = new NoteAdapter(context, notes);
                    listView.setAdapter(adapter);

                    // Smooth scroll to top
                    listView.post(new Runnable() {
                        public void run() {
                            listView.smoothScrollToPosition(0);
                        }
                    });
                }

                // If favoured note was first -> just update object in notes array and notify adapter
                else {
                    try {
                        notes.put(position, newFavourite);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapter.notifyDataSetChanged();
                }
            }

            // If note not favourite -> set favoured to false and notify adapter
            else {
                try {
                    newFavourite.put(NOTE_FAVOURED, false);
                    notes.put(position, newFavourite);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();
            }

            // Save notes to local file
            saveData(localPath, notes);
        }
    }

    /**
     * Orientation changed callback method
     * If orientation changed -> If any AlertDialog is showing, dismiss it to prevent WindowLeaks
     *
     * @param newConfig New Configuration passed by system
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (backupCheckDialog != null && backupCheckDialog.isShowing())
            backupCheckDialog.dismiss();

        if (backupOKDialog != null && backupOKDialog.isShowing())
            backupOKDialog.dismiss();

        if (restoreCheckDialog != null && restoreCheckDialog.isShowing())
            restoreCheckDialog.dismiss();

        if (restoreFailedDialog != null && restoreFailedDialog.isShowing())
            restoreFailedDialog.dismiss();

        super.onConfigurationChanged(newConfig);
    }


    // Static method to return File at localPath
    public static File getLocalPath() {
        return localPath;
    }

    // Static method to return File at backupPath
    public static File getBackupPath() {
        return backupPath;
    }
}

