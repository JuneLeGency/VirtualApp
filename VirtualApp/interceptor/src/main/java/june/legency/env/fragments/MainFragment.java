package june.legency.env.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.lc.puppet.client.local.interceptor.VInterceptorCallManager;
import com.lody.virtual.client.core.VirtualCore;

import java.util.ArrayList;
import java.util.List;

import june.legency.activities.EnvDetailActivity;
import june.legency.env.R;

/**
 * Created by lichen:) on 2017/2/6.
 */

public class MainFragment extends Fragment {

    private Switch envSwitch;
    private RecyclerView mRecyclerView;
    private ContentAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        envSwitch = (Switch) view.findViewById(R.id.env_switch);
        envSwitch.setChecked(VirtualCore.get().isInterceptorEnabled());
        envSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                VInterceptorCallManager.get().getInterface().setObFlowEnable(isChecked);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.envs_recycler_view);
        mAdapter = new ContentAdapter();
        mAdapter.setListener(new ItemListener() {
            @Override
            public void selected(int position) {
                positionChange(position);
            }

            @Override
            public void entered(int position) {
                Intent i = new Intent(getContext(), EnvDetailActivity.class);
                i.putExtra("env", mAdapter.envs.get(position));
                startActivity(i);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        try {
            List<String> envs = VInterceptorCallManager.get().getInterface().getEnvs();
            String current = VInterceptorCallManager.get().getInterface().getCurrentEnv();
            int position = envs.indexOf(current);
            mAdapter.setEnvs(envs);
            mAdapter.setSelection(position);
            mAdapter.notifyDataSetChanged();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    void positionChange(int position) {
        try {
            VInterceptorCallManager.get().getInterface().setCurrentEnv(mAdapter.envs.get(position));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mAdapter.setSelection(position);
        mAdapter.notifyDataSetChanged();
    }

    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        List<String> envs = new ArrayList<>();
        private int mSelection;

        public void setListener(ItemListener listener) {
            mListener = listener;
        }

        ItemListener mListener;

        ContentAdapter() {

        }

        public void setEnvs(List<String> envs) {
            this.envs = envs;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String env = envs.get(position);
            holder.text_key.setText(env);
            holder.selection_icon.setVisibility(position == mSelection ? View.VISIBLE : View.INVISIBLE);
            holder.itemListener = mListener;
        }

        @Override
        public int getItemCount() {
            return envs.size();
        }

        public void setSelection(int selection) {
            mSelection = selection;
        }
    }

    interface ItemListener {
        void selected(int position);

        void entered(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View selection_icon;
        private final View enter;
        public ItemListener itemListener = null;
        public TextView text_key;
        public TextView text_value;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_env, parent, false));
            text_key = (TextView) itemView.findViewById(R.id.text_key);
            text_value = (TextView) itemView.findViewById(R.id.text_value);
            selection_icon = itemView.findViewById(R.id.selection_icon);
            enter = itemView.findViewById(R.id.enter);

            itemView.setOnClickListener(v -> {
                if (itemListener != null)
                    itemListener.selected(getAdapterPosition());
            });
            enter.setOnClickListener(v -> {
                if (itemListener != null)
                    itemListener.entered(getAdapterPosition());
            });
        }
    }
}
