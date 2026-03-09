import Database from 'better-sqlite3';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

export function initDatabase() {
  const dbPath = path.join(__dirname, '../data.sqlite');
  const db = new Database(dbPath);

  // Create items table
  db.exec(`
    CREATE TABLE IF NOT EXISTS items (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      name TEXT NOT NULL,
      description TEXT,
      created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
      updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
    )
  `);

  // Insert sample data if table is empty
  const count = db.prepare('SELECT COUNT(*) as count FROM items').get();
  if (count.count === 0) {
    const insert = db.prepare('INSERT INTO items (name, description) VALUES (?, ?)');
    insert.run('Welcome Item', 'This is a sample item to get you started');
    insert.run('React', 'A JavaScript library for building user interfaces');
    insert.run('Node.js', 'JavaScript runtime built on Chrome V8 engine');
    insert.run('SQLite', 'Self-contained, serverless SQL database engine');
    console.log('📝 Inserted sample data');
  }

  console.log('✅ Database initialized');
  return db;
}
