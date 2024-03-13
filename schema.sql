CREATE TABLE IF NOT EXISTS folder (
    id UUID NOT NULL,
    parent UUID,
    name VARCHAR(50) NOT NULL,
    created TIMESTAMP NOT NULL,
    changed TIMESTAMP,
    changecounter INT NOT NULL DEFAULT 0,
    PRIMARY KEY(id),
    CONSTRAINT fk_folder
        FOREIGN KEY(parent)
            REFERENCES folder(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS file (
    id UUID NOT NULL,
    parent UUID,
    name VARCHAR(50) NOT NULL,
    created TIMESTAMP NOT NULL,
    changed TIMESTAMP,
    changecounter INT NOT NULL DEFAULT 0,
    extension VARCHAR(10) NOT NULL,
    islocked BOOL NOT NULL DEFAULT 'f',
    filesize DECIMAL NOT NULL DEFAULT 0,
    importance INT NOT NULL DEFAULT 0,
    password VARCHAR(64),
    content TEXT,
    PRIMARY KEY(id),
    CONSTRAINT fk_folder
        FOREIGN KEY(parent)
            REFERENCES folder(id) ON DELETE CASCADE
);
