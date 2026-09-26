import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  ArrowLeft,
  MapPin,
  CreditCard,
  Banknote,
  Smartphone,
  CheckCircle2,
} from "lucide-react";
import { useCart } from "../../context/CartContext";

type PaymentMethod = "COD" | "CARD" | "UPI";

function Checkout() {
  const navigate = useNavigate();

  const {
    items,
    subtotal,
    deliveryFee,
    tax,
    total,
    clearCart,
  } = useCart();

  const [paymentMethod, setPaymentMethod] =
    useState<PaymentMethod>("UPI");

  const [address, setAddress] = useState({
    name: "",
    phone: "",
    addressLine: "",
    city: "",
    pincode: "",
  });

  const [isPlacingOrder, setIsPlacingOrder] = useState(false);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement>,
  ) => {
    const { name, value } = e.target;

    setAddress((current) => ({
      ...current,
      [name]: value,
    }));
  };

  const handlePlaceOrder = () => {
    if (
      !address.name ||
      !address.phone ||
      !address.addressLine ||
      !address.city ||
      !address.pincode
    ) {
      alert("Please fill in all delivery details.");
      return;
    }

    if (items.length === 0) {
      navigate("/cart");
      return;
    }

    setIsPlacingOrder(true);

    setTimeout(() => {
      const orderId = `FF-${Date.now()}`;

      const order = {
        id: orderId,
        items,
        subtotal,
        deliveryFee,
        tax,
        total,
        paymentMethod,
        address,
        status: "PLACED",
        createdAt: new Date().toISOString(),
      };

      localStorage.setItem(
        "foodflow-latest-order",
        JSON.stringify(order),
      );

      clearCart();

      navigate("/order-success", {
        state: { orderId },
      });
    }, 1000);
  };

  if (items.length === 0) {
    return (
      <div className="mx-auto max-w-3xl px-4 py-20 text-center">
        <div className="text-6xl">🛒</div>

        <h1 className="mt-5 text-3xl font-bold">
          Your cart is empty
        </h1>

        <p className="mt-2 text-zinc-500">
          Add some delicious food before checking out.
        </p>

        <Link
          to="/restaurants"
          className="mt-6 inline-block rounded-xl bg-orange-500 px-6 py-3 font-semibold text-white hover:bg-orange-600"
        >
          Browse Restaurants
        </Link>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-7xl px-4 py-8">
      <Link
        to="/cart"
        className="mb-6 flex items-center gap-2 text-sm font-medium text-zinc-600 hover:text-orange-500"
      >
        <ArrowLeft size={18} />
        Back to cart
      </Link>

      <h1 className="text-4xl font-bold">
        Checkout
      </h1>

      <p className="mt-2 text-zinc-500">
        Complete your delivery and payment details.
      </p>

      <div className="mt-8 grid gap-8 lg:grid-cols-[1fr_400px]">
        {/* LEFT */}
        <div className="space-y-6">
          {/* DELIVERY ADDRESS */}
          <section className="rounded-2xl border bg-white p-6">
            <div className="flex items-center gap-3">
              <div className="rounded-xl bg-orange-100 p-3 text-orange-600">
                <MapPin size={22} />
              </div>

              <div>
                <h2 className="text-xl font-bold">
                  Delivery address
                </h2>

                <p className="text-sm text-zinc-500">
                  Where should we deliver your order?
                </p>
              </div>
            </div>

            <div className="mt-6 grid gap-4 md:grid-cols-2">
              <input
                name="name"
                value={address.name}
                onChange={handleChange}
                placeholder="Full name"
                className="rounded-xl border px-4 py-3 outline-none focus:border-orange-500"
              />

              <input
                name="phone"
                value={address.phone}
                onChange={handleChange}
                placeholder="Phone number"
                type="tel"
                className="rounded-xl border px-4 py-3 outline-none focus:border-orange-500"
              />

              <input
                name="addressLine"
                value={address.addressLine}
                onChange={handleChange}
                placeholder="House / Street / Area"
                className="rounded-xl border px-4 py-3 outline-none focus:border-orange-500 md:col-span-2"
              />

              <input
                name="city"
                value={address.city}
                onChange={handleChange}
                placeholder="City"
                className="rounded-xl border px-4 py-3 outline-none focus:border-orange-500"
              />

              <input
                name="pincode"
                value={address.pincode}
                onChange={handleChange}
                placeholder="Pincode"
                maxLength={6}
                className="rounded-xl border px-4 py-3 outline-none focus:border-orange-500"
              />
            </div>
          </section>

          {/* PAYMENT */}
          <section className="rounded-2xl border bg-white p-6">
            <h2 className="text-xl font-bold">
              Payment method
            </h2>

            <p className="mt-1 text-sm text-zinc-500">
              Choose how you want to pay.
            </p>

            <div className="mt-5 space-y-3">
              <PaymentOption
                selected={paymentMethod === "UPI"}
                onClick={() => setPaymentMethod("UPI")}
                icon={<Smartphone size={20} />}
                title="UPI"
                description="Pay using UPI"
              />

              <PaymentOption
                selected={paymentMethod === "CARD"}
                onClick={() => setPaymentMethod("CARD")}
                icon={<CreditCard size={20} />}
                title="Card"
                description="Credit or debit card"
              />

              <PaymentOption
                selected={paymentMethod === "COD"}
                onClick={() => setPaymentMethod("COD")}
                icon={<Banknote size={20} />}
                title="Cash on Delivery"
                description="Pay when your order arrives"
              />
            </div>
          </section>
        </div>

        {/* RIGHT */}
        <aside className="h-fit rounded-2xl border bg-white p-6 lg:sticky lg:top-24">
          <h2 className="text-xl font-bold">
            Order summary
          </h2>

          <div className="mt-5 space-y-4">
            {items.map((item) => (
              <div
                key={item.id}
                className="flex items-center justify-between gap-3"
              >
                <div className="flex min-w-0 items-center gap-3">
                  <span className="text-2xl">
                    {item.emoji}
                  </span>

                  <div className="min-w-0">
                    <p className="truncate font-medium">
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

          <div className="my-6 border-t" />

          <div className="space-y-3 text-sm">
            <div className="flex justify-between">
              <span className="text-zinc-500">
                Subtotal
              </span>

              <span>₹{subtotal}</span>
            </div>

            <div className="flex justify-between">
              <span className="text-zinc-500">
                Delivery fee
              </span>

              <span>₹{deliveryFee}</span>
            </div>

            <div className="flex justify-between">
              <span className="text-zinc-500">
                Taxes
              </span>

              <span>₹{tax}</span>
            </div>
          </div>

          <div className="my-5 border-t" />

          <div className="flex items-center justify-between">
            <span className="text-lg font-bold">
              Total
            </span>

            <span className="text-2xl font-bold text-orange-500">
              ₹{total}
            </span>
          </div>

          <button
            onClick={handlePlaceOrder}
            disabled={isPlacingOrder}
            className="mt-6 flex w-full items-center justify-center gap-2 rounded-xl bg-orange-500 px-5 py-4 font-bold text-white hover:bg-orange-600 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {isPlacingOrder ? (
              "Placing order..."
            ) : (
              <>
                <CheckCircle2 size={20} />
                Place Order
              </>
            )}
          </button>

          <p className="mt-3 text-center text-xs text-zinc-400">
            Your payment is simulated for development.
          </p>
        </aside>
      </div>
    </div>
  );
}

function PaymentOption({
  selected,
  onClick,
  icon,
  title,
  description,
}: {
  selected: boolean;
  onClick: () => void;
  icon: React.ReactNode;
  title: string;
  description: string;
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`flex w-full items-center gap-4 rounded-xl border p-4 text-left transition ${
        selected
          ? "border-orange-500 bg-orange-50"
          : "border-zinc-200 hover:border-orange-300"
      }`}
    >
      <div
        className={`rounded-lg p-2 ${
          selected
            ? "bg-orange-500 text-white"
            : "bg-zinc-100 text-zinc-600"
        }`}
      >
        {icon}
      </div>

      <div className="flex-1">
        <p className="font-semibold">
          {title}
        </p>

        <p className="text-sm text-zinc-500">
          {description}
        </p>
      </div>

      <div
        className={`h-5 w-5 rounded-full border-2 ${
          selected
            ? "border-orange-500 bg-orange-500"
            : "border-zinc-300"
        }`}
      >
        {selected && (
          <div className="m-1 h-2 w-2 rounded-full bg-white" />
        )}
      </div>
    </button>
  );
}

export default Checkout;

