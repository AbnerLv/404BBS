package com.bbs404.util;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.avos.avoscloud.*;
import com.bbs404.avobject.User;
import com.bbs404.base.C;
import com.bbs404.service.PreferenceMap;
import com.bbs404.ui.view.ViewHolder;
import com.bbs404.R;
import com.bbs404.adapter.BaseListAdapter;
import com.bbs404.base.App;
import com.bbs404.service.UserService;
import com.bbs404.ui.view.xlist.XListView;

import java.util.List;


public class ChatUtils {
    public static void handleListResult(XListView listView, BaseListAdapter adapter, List datas) {
        if (Utils.isListNotEmpty(datas)) {
            adapter.addAll(datas);
            if (datas.size() == C.PAGE_SIZE) {
                listView.setPullLoadEnable(true);
            } else {
                listView.setPullLoadEnable(false);
            }
        } else {
            listView.setPullLoadEnable(false);
            if (adapter.getCount() == 0) {
                Utils.toast(R.string.noResult);
            } else {
                Utils.toast(R.string.dataLoadFinish);
            }
        }
    }

    public static void updateUserInfo() {
        User user = User.curUser();
        if (user != null) {
            user.fetchInBackground(User.FRIENDS, new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    if (e == null) {
                        User avUser = (User) avObject;
                        App.registerUserCache(avUser);
                    }
                }
            });
        }
    }

    public static void updateUserLocation() {
        PreferenceMap preferenceMap = PreferenceMap.getCurUserPrefDao(App.ctx);
        AVGeoPoint lastLocation = preferenceMap.getLocation();
        if (lastLocation != null) {
            final User user = User.curUser();
            final AVGeoPoint location = user.getLocation();
            if (location == null || !Utils.doubleEqual(location.getLatitude(), lastLocation.getLatitude())
                    || !Utils.doubleEqual(location.getLongitude(), lastLocation.getLongitude())) {
                user.setLocation(lastLocation);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e != null) {
                            e.printStackTrace();
                        } else {
                            Logger.v("lastLocation save " + user.getLocation());
                        }
                    }
                });
            }
        }
    }

    public static void stopRefresh(XListView xListView) {
        if (xListView.getPullRefreshing()) {
            xListView.stopRefresh();
        }
    }

    public static void setUserView(View conView, User user) {
        ImageView avatarView = ViewHolder.findViewById(conView, R.id.avatar);
        TextView nameView = ViewHolder.findViewById(conView, R.id.username);
        UserService.displayAvatar(user.getAvatarUrl(), avatarView);
        nameView.setText(user.getUsername());
    }
}
