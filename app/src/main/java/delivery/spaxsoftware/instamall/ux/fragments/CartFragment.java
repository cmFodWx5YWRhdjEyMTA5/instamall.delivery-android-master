package delivery.spaxsoftware.instamall.ux.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import delivery.spaxsoftware.instamall.CONST;
import delivery.spaxsoftware.instamall.MyApplication;
import delivery.spaxsoftware.instamall.R;
import delivery.spaxsoftware.instamall.SettingsMy;
import delivery.spaxsoftware.instamall.api.EndPoints;
import delivery.spaxsoftware.instamall.api.GsonRequest;
import delivery.spaxsoftware.instamall.api.JsonRequest;
import delivery.spaxsoftware.instamall.entities.User;
import delivery.spaxsoftware.instamall.entities.cart.Cart;
import delivery.spaxsoftware.instamall.entities.cart.CartDiscountItem;
import delivery.spaxsoftware.instamall.entities.cart.CartProductItem;
import delivery.spaxsoftware.instamall.interfaces.CartRecyclerInterface;
import delivery.spaxsoftware.instamall.interfaces.RequestListener;
import delivery.spaxsoftware.instamall.listeners.OnSingleClickListener;
import delivery.spaxsoftware.instamall.utils.MsgUtils;
import delivery.spaxsoftware.instamall.utils.RecyclerDividerDecorator;
import delivery.spaxsoftware.instamall.utils.Utils;
import delivery.spaxsoftware.instamall.ux.MainActivity;
import delivery.spaxsoftware.instamall.ux.adapters.CartRecyclerAdapter;
import delivery.spaxsoftware.instamall.ux.dialogs.DiscountDialogFragment;
import delivery.spaxsoftware.instamall.ux.dialogs.LoginExpiredDialogFragment;
import delivery.spaxsoftware.instamall.ux.dialogs.UpdateCartItemDialogFragment;
import timber.log.Timber;

/**
 * Fragment handles shopping cart.
 */
public class CartFragment extends Fragment {

    private ProgressDialog progressDialog;

    private View emptyCart;
    private View cartFooter;

    private RecyclerView cartRecycler;
    private CartRecyclerAdapter cartRecyclerAdapter;

    // Footer views and variables
    private TextView cartItemCountTv;
    private TextView cartTotalPriceTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - onCreateView", this.getClass().getSimpleName());
        MainActivity.setActionBarTitle(getString(R.string.Shopping_cart));

        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        progressDialog = Utils.generateProgressDialog(getActivity(), false);
        prepareCartRecycler(view);

        emptyCart = view.findViewById(R.id.cart_empty);
        View emptyCartAction = view.findViewById(R.id.cart_empty_action);
        emptyCartAction.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                // Just open drawer menu.
                Activity activity = getActivity();
                if (activity instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) activity;
                    if (mainActivity.drawerFragment != null)
                        mainActivity.drawerFragment.toggleDrawerMenu();
                }
            }
        });

        cartFooter = view.findViewById(R.id.cart_footer);
        cartItemCountTv = (TextView) view.findViewById(R.id.cart_footer_quantity);
        cartTotalPriceTv = (TextView) view.findViewById(R.id.cart_footer_price);
        view.findViewById(R.id.cart_footer_action).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                DiscountDialogFragment discountDialog = DiscountDialogFragment.newInstance(new RequestListener() {
                    @Override
                    public void requestSuccess(long newId) {
                        getCartContent();
                    }

                    @Override
                    public void requestFailed(VolleyError error) {
                        MsgUtils.logAndShowErrorMessage(getActivity(), error);
                    }
                });

                if (discountDialog != null) {
                    discountDialog.show(getFragmentManager(), DiscountDialogFragment.class.getSimpleName());
                }
            }
        });

        Button order = (Button) view.findViewById(R.id.cart_order);
        order.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).onOrderCreateSelected();
                }
            }
        });

        getCartContent();
        return view;
    }

    private void getCartContent() {
        User user = SettingsMy.getActiveUser();
        if (user != null) {
            String url = String.format(EndPoints.CART, SettingsMy.getActualNonNullShop(getActivity()).getId()) + "&id=" + user.getId();

            progressDialog.show();
            GsonRequest<Cart> getCart = new GsonRequest<>(Request.Method.GET, url, null, Cart.class,
                    new Response.Listener<Cart>() {
                        @Override
                        public void onResponse(@NonNull Cart cart) {
                            if (progressDialog != null) progressDialog.cancel();

                            MainActivity.updateCartCountNotification();
                            if (cart.getItems() == null || cart.getItems().size() == 0) {
                                setCartVisibility(false);
                            } else {
                                setCartVisibility(true);
                                cartRecyclerAdapter.refreshItems(cart);

                                cartItemCountTv.setText(getString(R.string.format_quantity, cart.getProductCount()));
                                cartTotalPriceTv.setText(cart.getTotalPriceFormatted());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (progressDialog != null) progressDialog.cancel();
                    setCartVisibility(false);
                    Timber.e("Get request cart error: %s", error.getMessage());
                    MsgUtils.logAndShowErrorMessage(getActivity(), error);
                }
            }, getFragmentManager(), user.getAccessToken());
            getCart.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            getCart.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(getCart, CONST.CART_REQUESTS_TAG);
        } else {
            LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
            loginExpiredDialogFragment.show(getFragmentManager(), "loginExpiredDialogFragment");
        }
    }


    private void setCartVisibility(boolean visible) {
        if (visible) {
            if (emptyCart != null) emptyCart.setVisibility(View.GONE);
            if (cartRecycler != null) cartRecycler.setVisibility(View.VISIBLE);
            if (cartFooter != null) cartFooter.setVisibility(View.VISIBLE);
        } else {
            if (cartRecyclerAdapter != null) cartRecyclerAdapter.cleatCart();
            if (emptyCart != null) emptyCart.setVisibility(View.VISIBLE);
            if (cartRecycler != null) cartRecycler.setVisibility(View.GONE);
            if (cartFooter != null) cartFooter.setVisibility(View.GONE);
        }
    }

    private void prepareCartRecycler(View view) {
        this.cartRecycler = (RecyclerView) view.findViewById(R.id.cart_recycler);
        cartRecycler.addItemDecoration(new RecyclerDividerDecorator(getActivity()));
        cartRecycler.setItemAnimator(new DefaultItemAnimator());
        cartRecycler.setHasFixedSize(true);
        cartRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        cartRecyclerAdapter = new CartRecyclerAdapter(getActivity(), new CartRecyclerInterface() {
            @Override
            public void onProductUpdate(CartProductItem cartProductItem) {
                UpdateCartItemDialogFragment updateDialog = UpdateCartItemDialogFragment.newInstance(cartProductItem, new RequestListener() {
                    @Override
                    public void requestSuccess(long newId) {
                        getCartContent();
                    }

                    @Override
                    public void requestFailed(VolleyError error) {
                        MsgUtils.logAndShowErrorMessage(getActivity(), error);
                    }
                });

                if (updateDialog != null) {
                    updateDialog.show(getFragmentManager(), UpdateCartItemDialogFragment.class.getSimpleName());
                }
            }

            @Override
            public void onProductDelete(CartProductItem cartProductItem) {
                if (cartProductItem != null)
                    deleteItemFromCart(cartProductItem.getId(), false);
                else
                    Timber.e("Trying delete null cart item.");
            }

            @Override
            public void onDiscountDelete(CartDiscountItem cartDiscountItem) {
                if (cartDiscountItem != null)
                    deleteItemFromCart(cartDiscountItem.getId(), true);
                else
                    Timber.e("Trying delete null cart discount.");
            }

            @Override
            public void onProductSelect(long productId) {
                if (getActivity() instanceof MainActivity)
                    ((MainActivity) getActivity()).onProductSelected(productId);
            }

            private void deleteItemFromCart(long id, boolean isDiscount) {
                User user = SettingsMy.getActiveUser();
                if (user != null) {
                    String url;
                    if (isDiscount)
                        url = String.format(EndPoints.CART_DISCOUNTS_SINGLE, SettingsMy.getActualNonNullShop(getActivity()).getId(), id);
                    else
                        url = String.format(EndPoints.CART_ITEM_DELETE, SettingsMy.getActualNonNullShop(getActivity()).getId(), id);

                    progressDialog.show();
                    JsonRequest req = new JsonRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Timber.d("Delete item from cart: %s", response.toString());
                            getCartContent();
                            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE,
                                    getString(R.string.The_item_has_been_successfully_removed), MsgUtils.ToastLength.LONG);
                            if (progressDialog != null) progressDialog.cancel();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (progressDialog != null) progressDialog.cancel();
                            MsgUtils.logAndShowErrorMessage(getActivity(), error);
                        }
                    }, getFragmentManager(), user.getAccessToken());
                    req.setRetryPolicy(MyApplication.getDefaultRetryPolice());
                    req.setShouldCache(false);
                    MyApplication.getInstance().addToRequestQueue(req, CONST.CART_REQUESTS_TAG);
                } else {
                    LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                    loginExpiredDialogFragment.show(getFragmentManager(), "loginExpiredDialogFragment");
                }
            }
        });
        cartRecycler.setAdapter(cartRecyclerAdapter);
    }

    @Override
    public void onStop() {
        MyApplication.getInstance().cancelPendingRequests(CONST.CART_REQUESTS_TAG);
        if (progressDialog != null) progressDialog.cancel();
        super.onStop();
    }
}
