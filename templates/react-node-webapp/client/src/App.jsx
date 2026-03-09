import { useState, useEffect } from 'react'

function App() {
  const [items, setItems] = useState([])
  const [searchQuery, setSearchQuery] = useState('')
  const [newItemName, setNewItemName] = useState('')
  const [newItemDescription, setNewItemDescription] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  // Fetch items from API
  const fetchItems = async (query = '') => {
    try {
      setLoading(true)
      const url = query ? `/api/items?search=${encodeURIComponent(query)}` : '/api/items'
      const response = await fetch(url)
      if (!response.ok) throw new Error('Failed to fetch items')
      const data = await response.json()
      setItems(data)
      setError(null)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchItems()
  }, [])

  // Handle search
  const handleSearch = (e) => {
    e.preventDefault()
    fetchItems(searchQuery)
  }

  // Handle add item
  const handleAddItem = async (e) => {
    e.preventDefault()
    if (!newItemName.trim()) return

    try {
      const response = await fetch('/api/items', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name: newItemName, description: newItemDescription })
      })
      if (!response.ok) throw new Error('Failed to add item')
      setNewItemName('')
      setNewItemDescription('')
      fetchItems(searchQuery)
    } catch (err) {
      setError(err.message)
    }
  }

  // Handle delete item
  const handleDelete = async (id) => {
    try {
      const response = await fetch(`/api/items/${id}`, { method: 'DELETE' })
      if (!response.ok) throw new Error('Failed to delete item')
      fetchItems(searchQuery)
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div className="app">
      <header className="header">
        <h1>Welcome to ${PROJECT_NAME}</h1>
        <p className="subtitle">A simple search application</p>
      </header>

      <main className="main">
        {/* Search Section */}
        <section className="search-section">
          <form onSubmit={handleSearch} className="search-form">
            <input
              type="text"
              placeholder="Search items..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="search-input"
            />
            <button type="submit" className="btn btn-primary">Search</button>
            <button 
              type="button" 
              className="btn btn-secondary"
              onClick={() => { setSearchQuery(''); fetchItems(); }}
            >
              Clear
            </button>
          </form>
        </section>

        {/* Add Item Section */}
        <section className="add-section">
          <h2>Add New Item</h2>
          <form onSubmit={handleAddItem} className="add-form">
            <input
              type="text"
              placeholder="Item name"
              value={newItemName}
              onChange={(e) => setNewItemName(e.target.value)}
              className="input"
              required
            />
            <input
              type="text"
              placeholder="Description (optional)"
              value={newItemDescription}
              onChange={(e) => setNewItemDescription(e.target.value)}
              className="input"
            />
            <button type="submit" className="btn btn-primary">Add Item</button>
          </form>
        </section>

        {/* Error Message */}
        {error && <div className="error">Error: {error}</div>}

        {/* Items List */}
        <section className="items-section">
          <h2>Items {items.length > 0 && `(${items.length})`}</h2>
          {loading ? (
            <p className="loading">Loading...</p>
          ) : items.length === 0 ? (
            <p className="empty">No items found. Add one above!</p>
          ) : (
            <ul className="items-list">
              {items.map((item) => (
                <li key={item.id} className="item">
                  <div className="item-content">
                    <strong>{item.name}</strong>
                    {item.description && <p>{item.description}</p>}
                    <small>Created: {new Date(item.created_at).toLocaleString()}</small>
                  </div>
                  <button 
                    onClick={() => handleDelete(item.id)} 
                    className="btn btn-danger"
                  >
                    Delete
                  </button>
                </li>
              ))}
            </ul>
          )}
        </section>
      </main>

      <footer className="footer">
        <p>&copy; 2024 ${AUTHOR_NAME} - ${PROJECT_NAME} v${PROJECT_VERSION}</p>
      </footer>
    </div>
  )
}

export default App
