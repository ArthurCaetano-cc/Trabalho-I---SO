export class DatabaseAccessControl {
  private activeReaders = 0;
  private writerActive = false;
  private readerQueue: (() => void)[] =
    [];
  private writerQueue: (() => void)[] =
    [];

  private waitForReaderSlot(): Promise<void> {
    return new Promise((resolve) => {
      this.readerQueue.push(resolve);
    });
  }

  private waitForWriterSlot(): Promise<void> {
    return new Promise((resolve) => {
      this.writerQueue.push(resolve);
    });
  }

  private async acquireReader(
    id: number
  ): Promise<void> {
    while (
      this.writerActive ||
      this.activeReaders >= 10
    ) {
      await this.waitForReaderSlot();
    }

    this.activeReaders++;
    console.log(
      `🔎 Leitor ${id} iniciou leitura`
    );
  }

  private releaseReader(id: number) {
    this.activeReaders--;
    console.log(
      `✅ Leitor ${id} terminou leitura`
    );
    this.wakeUp();
  }

  async read(id: number) {
    await this.acquireReader(id);
    await this.simulateOperation();
    this.releaseReader(id);
  }

  async write(
    id: number,
    operation: string
  ) {
    while (
      this.writerActive ||
      this.activeReaders > 0
    ) {
      await this.waitForWriterSlot();
    }

    this.writerActive = true;
    console.log(
      `✍️ Escritor ${id} iniciou ${operation}`
    );

    await this.simulateOperation();

    this.writerActive = false;
    console.log(
      `✅ Escritor ${id} terminou ${operation}`
    );

    this.wakeUp();
  }

  private wakeUp() {
    if (
      !this.writerActive &&
      this.activeReaders === 0 &&
      this.writerQueue.length
    ) {
      const nextWriter =
        this.writerQueue.shift();
      nextWriter?.();
    } else {
      while (
        this.activeReaders < 10 &&
        !this.writerActive &&
        this.readerQueue.length
      ) {
        const nextReader =
          this.readerQueue.shift();
        nextReader?.();
        this.activeReaders++;
      }
    }
  }

  private async simulateOperation(): Promise<void> {
    return new Promise((resolve) =>
      setTimeout(resolve, 2000)
    );
  }
}
