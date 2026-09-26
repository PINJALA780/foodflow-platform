import { Link } from "react-router-dom";
import { Package, ArrowRight, Clock3 } from "lucide-react";

interface StoredOrder {
  id: string;
  total: number;
  paymentMethod: string;
  status: string;
  createdAt: string;
  items: {
    id: string;
    name: string;
    price: number;
    quantity: number;
    emoji: string;
  }[];
}

function Orders() {
  const storedOrder = localStorage.getItem("foodflow-latest-order");

  const order: StoredOrder | null = storedOrder
    ? JSON.parse(storedOrder)
    : null;

  if (!order) {
    return (
      <div className="mx-auto max-w-3xl px-4 py-20 text-center">
        <div className="mx-auto flex h-20 w-20 items-center justify-center rounded-full bg-orange-100 text-orange-500">
          <Package size={40} />
        </div>

        <h1 className="mt-6 text-3xl font-bold">
          No orders yet
        </h1>

        <p className="mt-2 text-zinc-500">
          Your placed orders will appear here.
        </p>

        <Link
          to="/restaurants"
          className="mt-6 inline-flex rounded-xl bg-orange-500 px-6 py-3 font-semibold text-white hover:bg-orange-600"
        >
          Browse Restaurants
        </Link>
      </div>
    );
  }

  const orderDate = new Date(order.createdAt);

  return (
    <div className="mx-auto max-w-5xl px-4 py-8">
      <div>
        <h1 className="text-4xl font-bold">
          My Orders
        </h1>

        <p className="mt-2 text-zinc-500">
          Track your recent FoodFlow orders.
        </p>
      </div>

      <div className="mt-8 rounded-2xl border bg-white p-6 shadow-sm">
        <div className="flex flex-col justify-between gap-4 md:flex-row md:items-center">
          <div className="flex items-center gap-4">
            <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-orange-100 text-orange-500">
              <Package size={24} />
            </div>

            <div>
              <p className="font-bold">
                {order.id}
              </p>

              <p className="mt-1 text-sm text-zinc-500">
                {orderDate.toLocaleDateString()} ·{" "}
                {orderDate.toLocaleTimeString([], {
                  hour: "2-digit",
                  minute: "2-digit",
                })}
              </p>
            </div>
          </div>

          <div className="flex items-center gap-3">
            <span className="flex items-center gap-1 rounded-full bg-orange-100 px-3 py-1.5 text-sm font-semibold text-orange-600">
              <Clock3 size={15} />
              {order.status}
            </span>

            <span className="text-xl font-bold">
              ₹{order.total}
            </span>
          </div>
        </div>

        <div className="my-6 border-t" />

        <div className="space-y-3">
          {order.items.map((item) => (
            <div
              key={item.id}
              className="flex items-center justify-between rounded-xl bg-zinc-50 p-3"
            >
              <div className="flex items-center gap-3">
                <span className="text-2xl">
                  {item.emoji}
                </span>

                <div>
                  <p className="font-semibold">
                    {item.name}
                  </p>

                  <p className="text-sm text-zinc-500">
                    Qty {item.quantity}
                  </p>
                </div>
              </div>

              <span className="font-semibold">
                ₹{item.price * item.quantity}
              </span>
            </div>
          ))}
        </div>

        <div className="mt-6 flex flex-col justify-between gap-4 border-t pt-5 sm:flex-row sm:items-center">
          <div>
            <p className="text-sm text-zinc-500">
              Payment
            </p>

            <p className="font-semibold">
              {order.paymentMethod}
            </p>
          </div>

          <Link
            to={`/orders/${order.id}`}
            className="flex items-center justify-center gap-2 rounded-xl bg-orange-500 px-5 py-3 font-semibold text-white hover:bg-orange-600"
          >
            View Order
            <ArrowRight size={18} />
          </Link>
        </div>
      </div>
    </div>
  );
}

export default Orders;

