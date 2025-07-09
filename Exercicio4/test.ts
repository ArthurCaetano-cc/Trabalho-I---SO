import { DatabaseAccessControl } from "./database";

const db = new DatabaseAccessControl();

async function run() {
  const promises = [];

  for (let i = 1; i <= 15; i++) {
    promises.push(
      new Promise((resolve) => {
        setTimeout(() => {
          db.read(i).then(resolve);
        }, i * 300);
      })
    );
  }

  setTimeout(() => {
    db.write(1, "INSERT");
  }, 4000);

  setTimeout(() => {
    db.write(2, "DELETE");
  }, 8000);

  await Promise.all(promises);
}

run();
