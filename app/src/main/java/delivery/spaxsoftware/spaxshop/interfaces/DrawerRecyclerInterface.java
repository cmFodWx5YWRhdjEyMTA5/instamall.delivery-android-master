package delivery.spaxsoftware.spaxshop.interfaces;

import android.view.View;

import delivery.spaxsoftware.spaxshop.entities.drawerMenu.DrawerItemCategory;
import delivery.spaxsoftware.spaxshop.entities.drawerMenu.DrawerItemPage;

public interface DrawerRecyclerInterface {

    void onCategorySelected(View v, DrawerItemCategory drawerItemCategory);

    void onPageSelected(View v, DrawerItemPage drawerItemPage);

    void onHeaderSelected();
}
