import {
  Search,
  MapPin,
  Star,
  ArrowRight,
} from "lucide-react";

const categories = [
  { name: "Pizza", emoji: "🍕" },
  { name: "Burgers", emoji: "🍔" },
  { name: "Biryani", emoji: "🍗" },
  { name: "Healthy", emoji: "🥗" },
  { name: "Noodles", emoji: "🍜" },
  { name: "South Indian", emoji: "🥘" },
];

const restaurants = [
  {
    name: "Spice Garden",
    cuisine: "Indian • Biryani • North Indian",
    rating: "4.8",
    time: "25-30 min",
    image: "🍛",
  },
  {
    name: "Urban Pizza",
    cuisine: "Pizza • Italian • Fast Food",
    rating: "4.6",
    time: "20-25 min",
    image: "🍕",
  },
  {
    name: "Burger House",
    cuisine: "Burgers • Fast Food",
    rating: "4.7",
    time: "15-20 min",
    image: "🍔",
  },
];

function Home() {
  return (
    <div>
      {/* Hero */}
      <section className="bg-white">
        <div className="mx-auto grid max-w-7xl items-center gap-12 px-4 py-16 md:grid-cols-2 md:py-24">
          <div>
            <p className="mb-4 font-semibold tracking-wide text-orange-500">
              YOUR FOOD. YOUR FLOW.
            </p>

            <h1 className="text-5xl font-bold leading-tight tracking-tight md:text-6xl">
              Delicious food,
              <br />
              delivered fast.
            </h1>

            <p className="mt-6 max-w-xl text-lg leading-8 text-zinc-600">
              Discover great restaurants, explore delicious menus, and get your
              favorite meals delivered right to your doorstep.
            </p>

            <div className="mt-8 flex max-w-xl items-center rounded-2xl border bg-white p-2 shadow-lg">
              <Search className="mx-3 text-zinc-400" size={22} />

              <input
                type="text"
                placeholder="Search restaurants or dishes..."
                className="min-w-0 flex-1 bg-transparent px-2 py-3 outline-none"
              />

              <button className="rounded-xl bg-orange-500 px-6 py-3 font-semibold text-white transition hover:bg-orange-600">
                Search
              </button>
            </div>

            <div className="mt-5 flex items-center gap-2 text-sm text-zinc-500">
              <MapPin size={17} className="text-orange-500" />
              Delivering deliciousness to your doorstep
            </div>
          </div>

          <div className="flex min-h-[350px] items-center justify-center rounded-3xl bg-orange-50 text-[150px]">
            🍽️
          </div>
        </div>
      </section>

      {/* Categories */}
      <section className="mx-auto max-w-7xl px-4 py-14">
        <div className="mb-7 flex items-end justify-between">
          <div>
            <p className="font-medium text-orange-500">EXPLORE</p>
            <h2 className="mt-1 text-3xl font-bold">What are you craving?</h2>
          </div>

          <button className="hidden items-center gap-2 font-medium text-orange-500 sm:flex">
            View all <ArrowRight size={18} />
          </button>
        </div>

        <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-6">
          {categories.map((category) => (
            <button
              key={category.name}
              className="rounded-2xl border bg-white p-5 transition hover:-translate-y-1 hover:border-orange-300 hover:shadow-md"
            >
              <div className="text-4xl">{category.emoji}</div>
              <p className="mt-3 font-semibold">{category.name}</p>
            </button>
          ))}
        </div>
      </section>

      {/* Restaurants */}
      <section className="bg-white">
        <div className="mx-auto max-w-7xl px-4 py-14">
          <div className="mb-7">
            <p className="font-medium text-orange-500">TOP PICKS</p>
            <h2 className="mt-1 text-3xl font-bold">Popular restaurants</h2>
            <p className="mt-2 text-zinc-500">
              Highly rated restaurants loved by FoodFlow customers.
            </p>
          </div>

          <div className="grid gap-6 md:grid-cols-3">
            {restaurants.map((restaurant) => (
              <article
                key={restaurant.name}
                className="overflow-hidden rounded-2xl border bg-white transition hover:-translate-y-1 hover:shadow-xl"
              >
                <div className="flex h-52 items-center justify-center bg-zinc-100 text-8xl">
                  {restaurant.image}
                </div>

                <div className="p-5">
                  <div className="flex items-start justify-between gap-3">
                    <h3 className="text-xl font-bold">{restaurant.name}</h3>

                    <span className="flex items-center gap-1 rounded-lg bg-green-600 px-2 py-1 text-sm font-semibold text-white">
                      <Star size={13} fill="currentColor" />
                      {restaurant.rating}
                    </span>
                  </div>

                  <p className="mt-2 text-sm text-zinc-500">
                    {restaurant.cuisine}
                  </p>

                  <div className="mt-5 flex items-center justify-between border-t pt-4 text-sm text-zinc-500">
                    <span>{restaurant.time}</span>
                    <span>Free delivery</span>
                  </div>
                </div>
              </article>
            ))}
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="mx-auto max-w-7xl px-4 py-14">
        <div className="rounded-3xl bg-orange-500 px-8 py-12 text-white md:px-14">
          <h2 className="text-3xl font-bold md:text-4xl">
            Hungry? Let's get your order started.
          </h2>

          <p className="mt-3 max-w-xl text-orange-50">
            Explore restaurants and discover your next favorite meal with
            FoodFlow.
          </p>

          <button className="mt-7 rounded-xl bg-white px-6 py-3 font-semibold text-orange-600 transition hover:bg-orange-50">
            Explore Restaurants
          </button>
        </div>
      </section>
    </div>
  );
}

export default Home;
