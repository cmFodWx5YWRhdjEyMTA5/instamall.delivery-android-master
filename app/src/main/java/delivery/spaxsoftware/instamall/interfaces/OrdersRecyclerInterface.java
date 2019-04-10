package delivery.spaxsoftware.instamall.interfaces;

import android.view.View;

import delivery.spaxsoftware.instamall.entities.order.Order;

public interface OrdersRecyclerInterface {

    void onOrderSelected(View v, Order order);

}
