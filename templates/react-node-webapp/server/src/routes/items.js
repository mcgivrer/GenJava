import express from 'express';

const router = express.Router();

// GET /api/items - Get all items or search
router.get('/', (req, res) => {
  try {
    const { search } = req.query;
    let items;

    if (search) {
      const stmt = req.db.prepare(`
        SELECT * FROM items 
        WHERE name LIKE ? OR description LIKE ?
        ORDER BY created_at DESC
      `);
      const searchTerm = `%${search}%`;
      items = stmt.all(searchTerm, searchTerm);
    } else {
      const stmt = req.db.prepare('SELECT * FROM items ORDER BY created_at DESC');
      items = stmt.all();
    }

    res.json(items);
  } catch (error) {
    console.error('Error fetching items:', error);
    res.status(500).json({ error: 'Failed to fetch items' });
  }
});

// GET /api/items/:id - Get item by ID
router.get('/:id', (req, res) => {
  try {
    const stmt = req.db.prepare('SELECT * FROM items WHERE id = ?');
    const item = stmt.get(req.params.id);

    if (!item) {
      return res.status(404).json({ error: 'Item not found' });
    }

    res.json(item);
  } catch (error) {
    console.error('Error fetching item:', error);
    res.status(500).json({ error: 'Failed to fetch item' });
  }
});

// POST /api/items - Create new item
router.post('/', (req, res) => {
  try {
    const { name, description } = req.body;

    if (!name || !name.trim()) {
      return res.status(400).json({ error: 'Name is required' });
    }

    const stmt = req.db.prepare('INSERT INTO items (name, description) VALUES (?, ?)');
    const result = stmt.run(name.trim(), description?.trim() || null);

    const newItem = req.db.prepare('SELECT * FROM items WHERE id = ?').get(result.lastInsertRowid);
    res.status(201).json(newItem);
  } catch (error) {
    console.error('Error creating item:', error);
    res.status(500).json({ error: 'Failed to create item' });
  }
});

// PUT /api/items/:id - Update item
router.put('/:id', (req, res) => {
  try {
    const { name, description } = req.body;
    const { id } = req.params;

    if (!name || !name.trim()) {
      return res.status(400).json({ error: 'Name is required' });
    }

    const existing = req.db.prepare('SELECT * FROM items WHERE id = ?').get(id);
    if (!existing) {
      return res.status(404).json({ error: 'Item not found' });
    }

    const stmt = req.db.prepare(`
      UPDATE items 
      SET name = ?, description = ?, updated_at = CURRENT_TIMESTAMP 
      WHERE id = ?
    `);
    stmt.run(name.trim(), description?.trim() || null, id);

    const updatedItem = req.db.prepare('SELECT * FROM items WHERE id = ?').get(id);
    res.json(updatedItem);
  } catch (error) {
    console.error('Error updating item:', error);
    res.status(500).json({ error: 'Failed to update item' });
  }
});

// DELETE /api/items/:id - Delete item
router.delete('/:id', (req, res) => {
  try {
    const { id } = req.params;

    const existing = req.db.prepare('SELECT * FROM items WHERE id = ?').get(id);
    if (!existing) {
      return res.status(404).json({ error: 'Item not found' });
    }

    const stmt = req.db.prepare('DELETE FROM items WHERE id = ?');
    stmt.run(id);

    res.json({ message: 'Item deleted successfully' });
  } catch (error) {
    console.error('Error deleting item:', error);
    res.status(500).json({ error: 'Failed to delete item' });
  }
});

export default router;
