package xnt.com.fun.tiantu;

import android.view.View;
import android.widget.TextView;

import xnt.com.fun.R;

public class CommentViewHolder {
    private View mRootView;
    public final View writeCommentView;
    public final TextView showCommentView;
    public final TextView praiseView;
    public final TextView descView;
    public CommentViewHolder(View rootView){
        writeCommentView = rootView.findViewById(R.id.write_comment_tv);
        showCommentView = rootView.findViewById(R.id.show_comment_tv);
        praiseView = rootView.findViewById(R.id.pic_praise_tv);
        descView = rootView.findViewById(R.id.pic_desc);
    }
}
