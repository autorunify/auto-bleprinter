import { registerPlugin } from '@capacitor/core';

import type { BlePrinterPlugin } from './definitions';

const BlePrinter = registerPlugin<BlePrinterPlugin>('BlePrinter', {
  web: () => import('./web').then((m) => new m.BlePrinterWeb()),
});


export * from './image'
export * from './definitions';
export { BlePrinter };
