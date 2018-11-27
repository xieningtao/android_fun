package xnt.com.fun.tiantu;

import android.view.View;
import android.widget.TextView;

import xnt.com.fun.R;

public class CommentViewHolder {
    public final View writeCommentView;
    public final TextView showCommentNumTv;
    public final View showCommentLl;
    public final TextView praiseView;
    public final TextView descView;
    public CommentViewHolder(View rootView){
        writeCommentView = rootView.findViewById(R.id.write_comment_tv);
        showCommentNumTv = rootView.findViewById(R.id.show_comment_tv);
        showCommentLl = rootView.findViewById(R.id.show_comment_ll);
        praiseView = rootView.findViewById(R.id.pic_praise_tv);
        descView = rootView.findViewById(R.id.pic_desc);
    }
}
