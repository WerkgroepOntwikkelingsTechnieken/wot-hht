CREATE TABLE tool (
	id INT PRIMARY KEY,
    name CHAR(255),
    description CHAR(255)
);

CREATE TABLE team (
	id INT PRIMARY KEY,
    name CHAR(255),
    active BOOL,
    warnings INT
);

CREATE TABLE has (
	team_id INT,
    tool_id INT,
    FOREIGN KEY (team_id) REFERENCES team(id),
    FOREIGN KEY (tool_id) REFERENCES tool(id),
    PRIMARY KEY (team_id, tool_id)
);

CREATE TABLE history (
	id INT PRIMARY KEY AUTO_INCREMENT,
	team_id INT,
    tool_id INT,
    time_stamp TIMESTAMP,
    FOREIGN KEY (team_id) REFERENCES team(id),
    FOREIGN KEY (tool_id) REFERENCES tool(id)
);