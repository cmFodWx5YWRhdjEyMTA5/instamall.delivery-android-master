package delivery.spaxsoftware.instamall.interfaces;

import android.view.View;

import delivery.spaxsoftware.instamall.entities.drawerMenu.DrawerItemCategory;
import delivery.spaxsoftware.instamall.entities.drawerMenu.DrawerItemPage;

public interface DrawerRecyclerInterface {

    void onCategorySelected(View v, DrawerItemCategory drawerItemCategory);

    void onPageSelected(View v, DrawerItemPage drawerItemPage);

    void onHeaderSelected();
}
