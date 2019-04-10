package delivery.spaxsoftware.instamall.interfaces;

import android.view.View;

import delivery.spaxsoftware.instamall.entities.wishlist.WishlistItem;

public interface WishlistInterface {

    void onWishlistItemSelected(View view, WishlistItem wishlistItem);

    void onRemoveItemFromWishList(View caller, WishlistItem wishlistItem, int adapterPosition);
}
