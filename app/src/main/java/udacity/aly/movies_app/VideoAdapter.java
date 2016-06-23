package udacity.aly.movies_app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by aly on 23/06/16.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private final List<MovieContent.Video> mValues;
    private Context mContext;

    public VideoAdapter(Context context, List<MovieContent.Video> items) {
        mValues = items;
        mContext = context;
    }

    public void addVideo(MovieContent.Video v) {
        mValues.add(v);
    }

    public void clear() {
        mValues.clear();
    }

    @Override
    public VideoAdapter.VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_list_content, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VideoViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).getName());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String youtubeUrl = "http://www.youtube.com/watch?v=" + holder.mItem.getKey();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public MovieContent.Video mItem;

        public VideoViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.video_text);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + this.mItem.getName() + "'";
        }
    }
}