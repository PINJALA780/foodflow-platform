import { Link, useParams } from "react-router-dom";
import { ArrowLeft, Star, Plus, Check } from "lucide-react";
import { useCart } from "../../context/CartContext";
import { useState } from "react";

const menuItems = [
  {
    id: "chicken-biryani",
    name: "Chicken Biryani",
    description: "Aromatic basmati rice with tender chicken and spices.",
    price: 249,
    emoji: "🍗",
  },
  {
    id: "paneer-biryani",
    name: "Paneer Biryani",
    description: "Fragrant basmati rice with spicy paneer and herbs.",
    price: 199,
    emoji: "🥘",
  },
  {
    id: "butter-chicken",
    name: "Butter Chicken",
    description: "Creamy tomato gravy with tender chicken.",
    price: 279,
    emoji: "🍛",
  },
  {
    id: "veg-fried-rice",
    name: "Veg Fried Rice",
    description: "Wok-tossed rice with fresh vegetables.",
    price: 169,
    emoji: "🍚",
  },
];

function RestaurantDetails() {
  const { id } = useParams();
  const { addItem } = useCart();

  const [addedItem, setAddedItem] = useState<string | null>(null);

  const restaurantName =
    id === "urban-pizza"
      ? "Urban Pizza"
      : id === "burger-house"
        ? "Burger House"
        : id === "hyderabad-biryani-hub"
          ? "Hyderabad Biryani Hub"
          : id === "south-indian-kitchen"
            ? "South Indian Kitchen"
            : "Spice Garden";

  const handleAddToCart = (item: (typeof menuItems)[number]) => {
    addItem({
      id: item.id,
      name: item.name,
      price: item.price,
      emoji: item.emoji,
      restaurantId: id || "spice-garden",
    });

    setAddedItem(item.id);

    setTimeout(() => {
      setAddedItem(null);
    }, 1200);
  };

  return (
    <div className="mx-auto max-w-7xl px-4 py-8">
      <Link
        to="/restaurants"
        className="mb-6 flex items-center gap-2 text-sm font-medium text-zinc-600 hover:text-orange-500"
      >
        <ArrowLeft size={18} />
        Back to restaurants
      </Link>

      <section className="overflow-hidden rounded-3xl bg-white shadow-sm">
        <div className="flex min-h-[260px] items-center justify-center bg-orange-50 text-[120px]">
          🍛
        </div>

        <div className="p-6 md:p-8">
          <div className="flex flex-col justify-between gap-4 md:flex-row">
            <div>
              <p className="text-sm font-medium text-orange-500">
                RESTAURANT
              </p>

              <h1 className="mt-1 text-4xl font-bold">
                {restaurantName}
              </h1>

              <p className="mt-2 text-zinc-500">
                Indian • Biryani • North Indian
              </p>
            </div>

            <div className="flex h-fit items-center gap-2 rounded-xl bg-green-600 px-4 py-3 font-semibold text-white">
              <Star size={18} fill="currentColor" />
              4.8
            </div>
          </div>

          <div className="mt-6 flex gap-6 border-t pt-5 text-sm text-zinc-500">
            <span>25-30 min</span>
            <span>Free delivery</span>
            <span>₹₹</span>
          </div>
        </div>
      </section>

      <section className="py-10">
        <h2 className="text-3xl font-bold">Popular menu</h2>

        <div className="mt-6 grid gap-5 md:grid-cols-2">
          {menuItems.map((item) => {
            const isAdded = addedItem === item.id;

            return (
              <div
                key={item.id}
                className="flex gap-4 rounded-2xl border bg-white p-4"
              >
                <div className="flex h-24 w-24 shrink-0 items-center justify-center rounded-xl bg-zinc-100 text-4xl">
                  {item.emoji}
                </div>

                <div className="flex min-w-0 flex-1 flex-col">
                  <h3 className="font-bold">{item.name}</h3>

                  <p className="mt-1 text-sm text-zinc-500">
                    {item.description}
                  </p>

                  <div className="mt-auto flex items-center justify-between pt-3">
                    <span className="font-bold">
                      ₹{item.price}
                    </span>

                    <button
                      onClick={() => handleAddToCart(item)}
                      className={`flex items-center gap-1 rounded-lg px-3 py-2 text-sm font-semibold text-white transition ${
                        isAdded
                          ? "bg-green-600"
                          : "bg-orange-500 hover:bg-orange-600"
                      }`}
                    >
                      {isAdded ? (
                        <>
                          <Check size={16} />
                          Added
                        </>
                      ) : (
                        <>
                          <Plus size={16} />
                          Add
                        </>
                      )}
                    </button>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </section>
    </div>
  );
}

export default RestaurantDetails;

