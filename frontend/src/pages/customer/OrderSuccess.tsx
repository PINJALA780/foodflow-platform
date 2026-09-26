import { Link, useLocation } from "react-router-dom";
import { CheckCircle2, Package, ArrowRight } from "lucide-react";

function OrderSuccess() {
  const location = useLocation();

  const orderId =
    location.state?.orderId || "FF-PENDING";

  return (
    <div className="mx-auto max-w-2xl px-4 py-20 text-center">
      <div className="mx-auto flex h-20 w-20 items-center justify-center rounded-full bg-green-100 text-green-600">
        <CheckCircle2 size={44} />
      </div>

      <p className="mt-6 text-sm font-semibold uppercase tracking-wide text-green-600">
        Order placed successfully
      </p>

      <h1 className="mt-2 text-4xl font-bold">
        Your food is on the way!
      </h1>

      <p className="mx-auto mt-4 max-w-lg text-zinc-500">
        Your order has been placed successfully. You can
        track the order status from your orders page.
      </p>

      <div className="mx-auto mt-8 max-w-md rounded-2xl border bg-white p-6 text-left">
        <div className="flex items-center gap-4">
          <div className="rounded-xl bg-orange-100 p-3 text-orange-600">
            <Package size={24} />
          </div>

          <div>
            <p className="text-sm text-zinc-500">
              Order ID
            </p>

            <p className="font-bold">
              {orderId}
            </p>
          </div>
        </div>

        <div className="mt-5 rounded-xl bg-zinc-50 p-4">
          <p className="text-sm font-medium">
            Current status
          </p>

          <p className="mt-1 font-semibold text-orange-500">
            Order Placed
          </p>
        </div>
      </div>

      <div className="mt-8 flex flex-col justify-center gap-3 sm:flex-row">
        <Link
          to="/orders"
          className="flex items-center justify-center gap-2 rounded-xl bg-orange-500 px-6 py-3 font-semibold text-white hover:bg-orange-600"
        >
          View My Orders
          <ArrowRight size={18} />
        </Link>

        <Link
          to="/restaurants"
          className="rounded-xl border px-6 py-3 font-semibold hover:bg-zinc-50"
        >
          Continue Ordering
        </Link>
      </div>
    </div>
  );
}

export default OrderSuccess;

