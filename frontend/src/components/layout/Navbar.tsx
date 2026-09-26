import { Link, useNavigate } from "react-router-dom";
import { ShoppingBag, User, LogOut } from "lucide-react";
import { useAuth } from "../../context/AuthContext";
import { useCart } from "../../context/CartContext";

function Navbar() {
  const navigate = useNavigate();
  const { user, logout } = useAuth();
  const { items } = useCart();

  const cartCount = items.reduce(
    (total, item) => total + item.quantity,
    0,
  );

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  return (
    <nav className="sticky top-0 z-50 border-b bg-white/95 backdrop-blur">
      <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-4">
        <Link to="/" className="text-2xl font-bold">
          <span className="text-orange-500">Food</span>Flow
        </Link>

        <div className="hidden items-center gap-8 md:flex">
          <Link
            to="/"
            className="text-sm font-medium hover:text-orange-500"
          >
            Home
          </Link>

          <Link
            to="/restaurants"
            className="text-sm font-medium hover:text-orange-500"
          >
            Restaurants
          </Link>

          <Link
            to="/orders"
            className="text-sm font-medium hover:text-orange-500"
          >
            My Orders
          </Link>
        </div>

        <div className="flex items-center gap-3">
          {user ? (
            <>
              <div className="hidden items-center gap-2 sm:flex">
                <div className="flex h-9 w-9 items-center justify-center rounded-full bg-orange-100 text-orange-600">
                  <User size={18} />
                </div>

                <span className="max-w-32 truncate text-sm font-semibold">
                  {user.name}
                </span>
              </div>

              <button
                onClick={handleLogout}
                className="hidden items-center gap-2 rounded-xl px-4 py-2 text-sm font-medium hover:bg-zinc-100 sm:flex"
              >
                <LogOut size={18} />
                Logout
              </button>
            </>
          ) : (
            <Link
              to="/login"
              className="hidden items-center gap-2 rounded-xl px-4 py-2 text-sm font-medium hover:bg-zinc-100 sm:flex"
            >
              <User size={18} />
              Login
            </Link>
          )}

          <Link
            to="/cart"
            className="relative rounded-xl p-2 hover:bg-zinc-100"
          >
            <ShoppingBag size={21} />

            {cartCount > 0 && (
              <span className="absolute -right-1 -top-1 flex h-5 min-w-5 items-center justify-center rounded-full bg-orange-500 px-1 text-xs font-bold text-white">
                {cartCount}
              </span>
            )}
          </Link>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;

