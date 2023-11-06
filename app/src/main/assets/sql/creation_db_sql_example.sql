CREATE TABLE IF NOT EXISTS templates(
	'criteria' TEXT NOT NULL,
	'_name' TEXT NOT NULL,
	'_id' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT
);

CREATE TABLE IF NOT EXISTS notes(
	'candidates' TEXT NOT NULL,
	'template_id' INTEGER NOT NULL,
	'report' TEXT NOT NULL,
	'_name' TEXT NOT NULL,
	'_id' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	FOREIGN KEY ('template_id') REFERENCES templates('_id') ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS template_id_index ON templates('_id');
CREATE UNIQUE INDEX IF NOT EXISTS template_name_index ON templates('_name');

CREATE UNIQUE INDEX IF NOT EXISTS note_id_index ON notes('_id');
CREATE UNIQUE INDEX IF NOT EXISTS note_name_index ON notes('_name');
CREATE INDEX IF NOT EXISTS note_template_id_index ON notes('template_id');

INSERT INTO
templates ('_name', 'criteria')
VALUES
("name", '["criterion1", "criterion1", "criterion1", "criterion1", "criterion1"]');

SELECT * FROM templates;