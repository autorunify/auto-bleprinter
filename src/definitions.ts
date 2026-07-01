import type { PluginListenerHandle } from '@capacitor/core'
import type { ImageCompressor } from './image'

export type PermissionState = 'denied' | 'granted'

export type PermissionKey =
  "android.permission.BLUETOOTH" |
  "android.permission.BLUETOOTH_ADMIN" |
  "android.permission.ACCESS_COARSE_LOCATION" |
  "android.permission.ACCESS_FINE_LOCATION" |
  "android.permission.BLUETOOTH_SCAN" |
  "android.permission.BLUETOOTH_CONNECT"


export type Permissions = {
  [key in PermissionKey]: PermissionState;
}

export interface BluetoothState {
  enabled: boolean;
}

export interface ConnectionState {
  connected: boolean
}

export interface BleDevice {
  address: string
  name: string
  rssi: number
}

export interface OnStateChangeEvent {
  action: 'enabled' | 'connected'
  state: boolean
}

export interface TimeoutOptions {
  timeout?: number
}

export interface InitOptions {
  manufacturer: 'DotHanTech'
}

export interface ScanOptions extends TimeoutOptions {
  max?: number
}

export interface ConnectOptions extends TimeoutOptions {
  address: string
}

export interface PrintImageOptions {
  scale?: number,
  image: ImageCompressor
}

export interface PrintImageResults {
  state: 'success' | 'failed'
}

export interface ScanResults {
  devices: Array<BleDevice>
}

export interface BlePrinterPlugin {
  checkPermissions(): Promise<Permissions>;
  requestPermissions(): Promise<Permissions>;
  isBluetoothEnabled(): Promise<BluetoothState>;

  init(options: InitOptions): Promise<void>;
  kill(): Promise<void>

  devices(options?: ScanOptions): Promise<ScanResults>
  isConnected(): Promise<ConnectionState>;
  connect(options: ConnectOptions): Promise<void>;
  disconnect(options?: TimeoutOptions): Promise<void>;

  printImage(options: PrintImageOptions): Promise<PrintImageResults>


  addListener(
    event: 'state',
    callback: (e: OnStateChangeEvent) => void,
  ): Promise<PluginListenerHandle>;
}
