package delivery.spaxsoftware.spaxshop.interfaces;

import android.view.View;

import delivery.spaxsoftware.spaxshop.entities.wishlist.WishlistItem;

public interface WishlistInterface {

    void onWishlistItemSelected(View view, WishlistItem wishlistItem);

    void onRemoveItemFromWishList(View caller, WishlistItem wishlistItem, int adapterPosition);
}
