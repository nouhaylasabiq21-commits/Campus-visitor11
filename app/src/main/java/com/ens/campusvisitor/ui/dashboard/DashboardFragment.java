package com.ens.campusvisitor.ui.dashboard;

import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.ens.campusvisitor.R;
import com.ens.campusvisitor.api.ApiManager;
import com.ens.campusvisitor.ui.visits.VisitAdapter;
import com.ens.campusvisitor.utils.SessionManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private ApiManager api;
    private SessionManager session;
    private TextView tvGreeting, tvDate, tvPresent, tvToday, tvPending, tvRefused;
    private RecyclerView rvRecentVisits;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        api     = new ApiManager(requireContext());
        session = new SessionManager(requireContext());

        tvGreeting     = view.findViewById(R.id.tvGreeting);
        tvDate         = view.findViewById(R.id.tvDate);
        tvPresent      = view.findViewById(R.id.tvPresent);
        tvToday        = view.findViewById(R.id.tvToday);
        tvPending      = view.findViewById(R.id.tvPending);
        tvRefused      = view.findViewById(R.id.tvRefused);
        rvRecentVisits = view.findViewById(R.id.rvRecentVisits);
        swipeRefresh   = view.findViewById(R.id.swipeRefresh);

        rvRecentVisits.setLayoutManager(new LinearLayoutManager(requireContext()));

        String name = session.getUserName();
        if (name != null) {
            int hour = Integer.parseInt(new SimpleDateFormat("HH", Locale.getDefault()).format(new Date()));
            String gr = hour < 12 ? "Bonjour" : hour < 18 ? "Bon après-midi" : "Bonsoir";
            tvGreeting.setText(gr + ", " + name.split(" ")[0]);
        }
        tvDate.setText(new SimpleDateFormat("EEEE d MMMM yyyy", Locale.FRENCH).format(new Date()));

        swipeRefresh.setOnRefreshListener(this::loadData);
        loadData();
        return view;
    }

    private void loadData() {
        api.getDashboardStats(new ApiManager.ApiCallback() {
            @Override public void onSuccess(JSONObject r) {
                try {
                    tvPresent.setText(String.valueOf(r.getInt("visitors_present_now")));
                    tvToday.setText(String.valueOf(r.getInt("visits_today")));
                    tvPending.setText(String.valueOf(r.getInt("pending_visits")));
                    tvRefused.setText(String.valueOf(r.getInt("refused_visits") + r.getInt("cancelled_visits")));
                } catch (Exception e) { e.printStackTrace(); }
                swipeRefresh.setRefreshing(false);
            }
            @Override public void onError(String m) { swipeRefresh.setRefreshing(false); }
        });

        api.getVisits(null, new ApiManager.ArrayCallback() {
            @Override public void onSuccess(JSONArray r) {
                try {
                    int count = Math.min(r.length(), 6);
                    JSONArray recent = new JSONArray();
                    for (int i = 0; i < count; i++) recent.put(r.getJSONObject(i));
                    rvRecentVisits.setAdapter(new VisitAdapter(recent, requireContext(), null));
                } catch (Exception e) { e.printStackTrace(); }
            }
            @Override public void onError(String m) {}
        });
    }
}
