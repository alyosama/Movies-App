package udacity.aly.movies_app;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by aly on 23/06/16.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private final List<MovieContent.Review> mValues;

    public ReviewAdapter(List<MovieContent.Review> items) {
        mValues = items;
    }


    public void addReview(MovieContent.Review r) {
        mValues.add(r);
    }

    public void clear() {
        mValues.clear();
    }

    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_list_content, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReviewViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mAuthorView.setText(mValues.get(position).getAuthor());
        holder.mContentView.setText(mValues.get(position).getContent());

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mAuthorView;
        public final TextView mContentView;
        public MovieContent.Review mItem;

        public ReviewViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.review_text);
            mAuthorView = (TextView) view.findViewById(R.id.author_text);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + this.mItem.getId() + "'";
        }
    }
}