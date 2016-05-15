package dubsmashdemo.android.com.dubsmashdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import dubsmashdemo.android.com.dubsmashdemo.R;
import dubsmashdemo.android.com.dubsmashdemo.activities.VideoPlayerActivity;
import dubsmashdemo.android.com.dubsmashdemo.model.VideoObject;
import dubsmashdemo.android.com.dubsmashdemo.utils.Constants;

/**
 * Created by rahul.raja on 5/14/16.
 */

/**
 * {@link android.support.v7.widget.RecyclerView.Adapter} to show the videos list available
 */
public class VideoFilesAdapter extends RecyclerView.Adapter<VideoFilesAdapter.RecyclerItemViewHolder> {

    private List<VideoObject> mVideoObjects;

    public VideoFilesAdapter(List<VideoObject> videoObjects) {
        mVideoObjects = videoObjects;
    }

    public void updateAdapter(List<VideoObject> videoObjects) {
        mVideoObjects = videoObjects;
        notifyDataSetChanged();

    }

    @Override
    public RecyclerItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_row, parent, false);

        return new RecyclerItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerItemViewHolder holder, int position) {
        View holderView = holder.getView();
        final Context context = holderView.getContext();
        final VideoObject videoObject = mVideoObjects.get(position);
        holder.setTitle(videoObject.getVideoName());

        holderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoObject != null) {
                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra(Constants.KEY_VIDEO_PATH, videoObject.getVideoPath());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVideoObjects.size();
    }


    public static final class RecyclerItemViewHolder extends RecyclerView.ViewHolder {

        private View item;
        private TextView titleView;

        public RecyclerItemViewHolder(View itemView) {
            super(itemView);
            item = itemView;
            titleView = (TextView) item.findViewById(R.id.video_name);

        }

        public View getView() {
            return item;
        }


        public void setTitle(String title) {
            titleView.setText(title);
        }
    }
}
