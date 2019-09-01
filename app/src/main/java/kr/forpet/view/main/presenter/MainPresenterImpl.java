package kr.forpet.view.main.presenter;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import kr.forpet.data.entity.Shop;
import kr.forpet.map.MarkerBuilder;
import kr.forpet.view.main.model.MainModel;

public class MainPresenterImpl implements MainPresenter {

    private MainPresenter.View mView;
    private MainModel mainModel;

    public MainPresenterImpl() {
        mainModel = new MainModel();
    }

    @Override
    public void setView(View view) {
        this.mView = view;
    }

    @Override
    public void onCreate(Context context) {
        mainModel.initAppDatabase(context);
        mainModel.initGooglePlayService(context);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onMapSearch(Shop.CatCode catCode, LatLngBounds latLngBounds) {
        new AsyncTask<String, Void, List<Shop>>() {
            @Override
            protected List<Shop> doInBackground(String... strings) {
                return mainModel.getShopList(latLngBounds, strings[0]);
            }

            @Override
            protected void onPostExecute(List<Shop> shops) {
                super.onPostExecute(shops);

                for (Shop shop : shops) {
                    mView.addMarker(new MarkerBuilder(new LatLng(shop.getY(), shop.getX()))
                            .catCode(shop.getCategoryGroupCode())
                            .hash(shop.getForpetHash())
                            .title(shop.getPlaceName())
                            .event(shop.getEvent())
                            .build(mView.getContext()));
                }
            }
        }.execute(catCode.toString());
    }

    @Override
    public void onMarkerClick(Marker marker) {
        new AsyncTask<String, Void, Shop>() {
            @Override
            protected Shop doInBackground(String... strings) {
                return mainModel.getShop(strings[0]);
            }

            @Override
            protected void onPostExecute(Shop shop) {
                super.onPostExecute(shop);
                mView.showPopup(shop);
            }
        }.execute(marker.getSnippet().split(",")[0]);
    }

    @Override
    public void onMyLocate(OnCompleteListener<Location> listener) {
        try {
            Task task = mainModel.getMyLocation();
            task.addOnCompleteListener(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
