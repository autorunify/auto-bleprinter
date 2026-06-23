import { WebPlugin } from '@capacitor/core';

import type { BlePrinterPlugin, Permissions, BluetoothState, ConnectionState,PrintImageResults } from './definitions';

export class BlePrinterWeb extends WebPlugin implements BlePrinterPlugin {
  async checkPermissions(): Promise<Permissions> { return {} as Permissions }
  async requestPermissions(): Promise<Permissions> { return {} as Permissions }
  async isBluetoothEnabled(): Promise<BluetoothState> { return {} as BluetoothState }

  async init(): Promise<void> { }
  async kill(): Promise<void> { }
  async devices(): Promise<any> { }
  async isConnected(): Promise<ConnectionState> { return {} as ConnectionState }
  async connect(): Promise<void> { }
  async disconnect(): Promise<void> { }

  async printImage(): Promise<PrintImageResults> { return {} as PrintImageResults }
}
