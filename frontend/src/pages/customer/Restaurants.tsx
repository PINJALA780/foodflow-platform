import { Link } from "react-router-dom";
import { Search, Star } from "lucide-react";

const restaurants = [
  {
    id: "spice-garden",
    name: "Spice Garden",
    cuisine: "Indian • Biryani • North Indian",
    rating: "4.8",
    time: "25-30 min",
    image: "🍛",
  },
  {
    id: "urban-pizza",
    name: "Urban Pizza",
    cuisine: "Pizza • Italian • Fast Food",
    rating: "4.6",
    time: "20-25 min",
    image: "🍕",
  },
  {
    id: "burger-house",
    name: "Burger House",
    cuisine: "Burgers • Fast Food",
    rating: "4.7",
    time: "15-20 min",
    image: "🍔",
  },
  {
    id: "hyderabad-biryani-hub",
    name: "Hyderabad Biryani Hub",
    cuisine: "Biryani • Indian",
    rating: "4.9",
    time: "30-35 min",
    image: "🍗",
  },
  {
    id: "south-indian-kitchen",
    name: "South Indian Kitchen",
    cuisine: "South Indian • Breakfast",
    rating: "4.7",
    time: "20-25 min",
    image: "🥘",
  },
];

function Restaurants() {
  return (
    <div className="mx-auto max-w-7xl px-4 py-10">
      <div className="mb-8">
        <h1 className="text-4xl font-bold">Restaurants</h1>
        <p className="mt-2 text-zinc-500">
          Discover restaurants and delicious food near you.
        </p>
      </div>

      <div className="mb-10 flex items-center rounded-2xl border bg-white p-2 shadow-sm">
        <Search className="mx-3 text-zinc-400" />
        <input
          placeholder="Search restaurants..."
          className="flex-1 bg-transparent px-2 py-3 outline-none"
        />
      </div>

      <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {restaurants.map((restaurant) => (
          <Link
            key={restaurant.id}
            to={`/restaurants/${restaurant.id}`}
            className="overflow-hidden rounded-2xl border bg-white transition hover:-translate-y-1 hover:shadow-xl"
          >
            <div className="flex h-52 items-center justify-center bg-zinc-100 text-8xl">
              {restaurant.image}
            </div>

            <div className="p-5">
              <div className="flex items-start justify-between gap-3">
                <h2 className="text-xl font-bold">{restaurant.name}</h2>

                <span className="flex items-center gap-1 rounded-lg bg-green-600 px-2 py-1 text-sm font-semibold text-white">
                  <Star size={13} fill="currentColor" />
                  {restaurant.rating}
                </span>
              </div>

              <p className="mt-2 text-sm text-zinc-500">
                {restaurant.cuisine}
              </p>

              <div className="mt-5 border-t pt-4 text-sm text-zinc-500">
                {restaurant.time} • Free delivery
              </div>
            </div>
          </Link>
        ))}
      </div>
    </div>
  );
}

export default Restaurants;
