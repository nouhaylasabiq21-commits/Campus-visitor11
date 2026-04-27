package com.ens.campusvisitor.ui.visits;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.ens.campusvisitor.R;
import com.ens.campusvisitor.api.ApiManager;
import com.ens.campusvisitor.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONObject;

public class VisitsFragment extends Fragment {

    private ApiManager api;
    private VisitAdapter adapter;
    private JSONArray visitsData = new JSONArray();
    private SwipeRefreshLayout swipeRefresh;
    private String currentFilter = "";
    private SessionManager session;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visits, container, false);

        api     = new ApiManager(requireContext());
        session = new SessionManager(requireContext());

        RecyclerView rv = view.findViewById(R.id.rvVisits);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new VisitAdapter(visitsData, requireContext(), new VisitAdapter.ActionListener() {
            @Override public void onApprove(int id)  { updateStatus(id, "approved"); }
            @Override public void onRefuse(int id)   { updateStatus(id, "refused"); }
            @Override public void onCheckIn(int id)  { doCheckIn(id); }
            @Override public void onCheckOut(int id) { doCheckOut(id); }
        });
        rv.setAdapter(adapter);

        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this::loadVisits);

        LinearLayout llFilters = view.findViewById(R.id.llFilters);
        String[] filters = {"", "pending", "approved", "ongoing", "completed", "refused"};
        String[] labels  = {"Tous", "En attente", "Approuvée", "En cours", "Terminée", "Refusée"};
        for (int i = 0; i < filters.length; i++) {
            Button btn = new Button(requireContext());
            btn.setText(labels[i]); btn.setTextSize(11f);
            final String f = filters[i];
            btn.setOnClickListener(v -> { currentFilter = f; loadVisits(); });
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            p.setMargins(0, 0, 8, 0);
            btn.setLayoutParams(p);
            llFilters.addView(btn);
        }

        FloatingActionButton fab = view.findViewById(R.id.fabAddVisit);
        fab.setOnClickListener(v -> showCreateDialog());

        loadVisits();
        return view;
    }

    private void loadVisits() {
        api.getVisits(currentFilter, new ApiManager.ArrayCallback() {
            @Override public void onSuccess(JSONArray r) {
                visitsData = r; adapter.updateData(r); swipeRefresh.setRefreshing(false);
            }
            @Override public void onError(String m) { swipeRefresh.setRefreshing(false); }
        });
    }

    private void updateStatus(int id, String status) {
        api.updateVisitStatus(id, status, new ApiManager.ApiCallback() {
            @Override public void onSuccess(JSONObject r) { loadVisits(); toast("Statut mis à jour"); }
            @Override public void onError(String m)       { toast(m); }
        });
    }

    private void doCheckIn(int id) {
        api.checkIn(id, new ApiManager.ApiCallback() {
            @Override public void onSuccess(JSONObject r) { loadVisits(); toast("Check-in enregistré"); }
            @Override public void onError(String m)       { toast(m); }
        });
    }

    private void doCheckOut(int id) {
        api.checkOut(id, new ApiManager.ApiCallback() {
            @Override public void onSuccess(JSONObject r) { loadVisits(); toast("Check-out enregistré"); }
            @Override public void onError(String m)       { toast(m); }
        });
    }

    private void showCreateDialog() {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL); layout.setPadding(40, 20, 40, 20);

        EditText etVisitorId = new EditText(requireContext()); etVisitorId.setHint("ID Visiteur"); etVisitorId.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        EditText etHostId    = new EditText(requireContext()); etHostId.setHint("ID Hôte");       etHostId.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        EditText etPurpose   = new EditText(requireContext()); etPurpose.setHint("Objet");
        EditText etScheduled = new EditText(requireContext()); etScheduled.setHint("Date (2025-01-15T10:00:00)");
        EditText etNotes     = new EditText(requireContext()); etNotes.setHint("Notes");

        layout.addView(etVisitorId); layout.addView(etHostId);
        layout.addView(etPurpose);  layout.addView(etScheduled); layout.addView(etNotes);

        new AlertDialog.Builder(requireContext()).setTitle("Nouvelle visite").setView(layout)
            .setPositiveButton("Créer", (d, w) -> {
                try {
                    JSONObject body = new JSONObject();
                    body.put("visitor_id", Integer.parseInt(etVisitorId.getText().toString()));
                    body.put("host_id",    Integer.parseInt(etHostId.getText().toString()));
                    body.put("purpose",    etPurpose.getText().toString());
                    body.put("scheduled_at", etScheduled.getText().toString());
                    body.put("notes",      etNotes.getText().toString());
                    api.createVisit(body, new ApiManager.ApiCallback() {
                        @Override public void onSuccess(JSONObject r) { loadVisits(); toast("Visite créée"); }
                        @Override public void onError(String m)       { toast(m); }
                    });
                } catch (Exception e) { toast("Données invalides"); }
            })
            .setNegativeButton("Annuler", null).show();
    }

    private void toast(String msg) { Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show(); }
}
