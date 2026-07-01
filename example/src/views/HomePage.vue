<template>
  <ion-page>
    <ion-header :translucent="true">
      <ion-toolbar>
        <ion-title>Blank</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true">
      <ion-header collapse="condense">
        <ion-toolbar>
          <ion-title size="large">Blank</ion-title>
        </ion-toolbar>
      </ion-header>

      <div id="container">
        <canvas ref="canvasRef" width="300" height="600" style="border:1px solid #ccc"></canvas>

        <ion-list>
          <ion-item v-for="dev in devicesRef">
            <ion-label>{{ dev.address }}</ion-label>
            <ion-button slot="end" @click="onBlePrinterConnect(dev)">Conn</ion-button>
          </ion-item>
        </ion-list>

        <ion-button expand="full" @click="onNrfMeshInit" style="margin-bottom: 8px;">NrfMesh初始化</ion-button>
        <ion-button expand="full" @click="onNrfMeshKill" style="margin-bottom: 24px;">NrfMesh释放</ion-button>

        <ion-button expand="full" @click="onBlePrinterInit" style="margin-bottom: 8px;">BlePrinter初始化</ion-button>
        <ion-button expand="full" @click="onBlePrinterKill" style="margin-bottom: 8px;">BlePrinter释放</ion-button>
        <ion-button expand="full" @click="onBlePrinterScan" style="margin-bottom: 8px;">BlePrinter扫描</ion-button>
        <ion-button expand="full" @click="onBlePrinterDraw" style="margin-bottom: 8px;"
          :disabled="!connected">BlePrinter打印</ion-button>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { IonContent, IonHeader, IonPage, IonTitle, IonToolbar, IonButton, IonList, IonItem } from '@ionic/vue';
import { NrfMesh, PermissionKey } from '@autorunify/capacitor-nrfmesh'
import { BleDevice, BlePrinter, ImageCompressor, } from '@autorunify/capacitor-bleprinter'
import { computed, onMounted, ref, useTemplateRef } from 'vue';

const devicesRef = ref<Array<BleDevice>>([])
const connected = ref<Boolean>(false)
const canvasRef = useTemplateRef<HTMLCanvasElement>("canvasRef")

async function onNrfMeshInit() {
  const bleEnabled = await NrfMesh.isBluetoothEnabled()
  if (!bleEnabled.enabled) {
    console.log("BLE is not enabled")
    return
  }

  const permis = await NrfMesh.checkPermissions()
  const denied = Object.keys(permis).map(key => permis[key as PermissionKey]).filter(v => (v as PermissionState) == 'denied')

  console.log(denied)
  if (denied.length > 0) {
    await NrfMesh.requestPermissions()
  }

  await NrfMesh.init()
}

async function onNrfMeshKill() {
  await NrfMesh.kill()
}

async function onBlePrinterInit() {
  const bleEnabled = await BlePrinter.isBluetoothEnabled()
  if (!bleEnabled.enabled) {
    console.log("BLE is not enabled")
    return
  }

  const permis = await BlePrinter.checkPermissions()
  const denied = Object.keys(permis).map(key => permis[key as PermissionKey]).filter(v => (v as PermissionState) == 'denied')

  console.log(denied)
  if (denied.length > 0) {
    await BlePrinter.requestPermissions()
  }

  await BlePrinter.init({ manufacturer: 'DotHanTech' })
}

async function onBlePrinterKill() {
  await BlePrinter.kill()
}

async function onBlePrinterScan() {
  devicesRef.value = []

  const { devices } = await BlePrinter.devices({ max: 1 })
  devicesRef.value = devices


  await onBlePrinterConnect(devices[0])
  await onBlePrinterDraw()
  await BlePrinter.disconnect()
  await BlePrinter.kill()
}

async function onBlePrinterConnect(dev: BleDevice) {
  const { connected } = await BlePrinter.isConnected()

  console.log({ connected })

  if (connected == false) {
    await BlePrinter.connect({ address: dev.address })
  } else {
    await BlePrinter.disconnect()
  }
}

async function onBlePrinterDraw() {
  const width = canvasRef.value?.width as number
  const height = canvasRef.value?.height as number
  const ctx = canvasRef.value?.getContext('2d')

  console.log({ ctx, width, height })
  if (!ctx) return

  const imageData = ctx.getImageData(0, 0, width, height)

  await BlePrinter.printImage({
    scale: 1,
    image: new ImageCompressor(imageData)
  })

  // await BlePrinter.printImage({
  //   scale: 2,
  //   width,
  //   height,
  //   imageData: imageData.data
  // })

  //   await BlePrinter.printImage({
  //   scale: 0.5,
  //   width,
  //   height,
  //   imageData: imageData.data
  // })
}

onMounted(() => {
  NrfMesh.addListener('state', (e) => {
    console.log('NrfMesh', e)
  })

  BlePrinter.addListener('state', (e) => {
    console.log('BlePrinter', e)
    if (e.action == 'connected') {
      connected.value = e.state
    }
  })

  const width = canvasRef.value?.width as number
  const height = canvasRef.value?.height as number
  const ctx = canvasRef.value?.getContext('2d')
  if (!ctx) return

  ctx.fillStyle = 'white'
  ctx.fillRect(0, 0, width, height)

  // 1. 设置字体样式 (格式同 CSS)
  ctx.font = '30px Arial';

  // 2. 绘制实心文字 (文本, x坐标, y坐标)
  ctx.fillStyle = 'black';
  ctx.fillText('Hello Canvas', 10, 0);

  // 3. 绘制描边文字
  ctx.fillStyle = 'red';
  ctx.fillText('Red Canvas', 10, 30);

  ctx.fillStyle = 'green';
  ctx.fillText('Green Canvas', 10, 60);

  ctx.fillStyle = 'blue';
  ctx.fillText('Blue Canvas', 10, 90);

  ctx.fillStyle = 'yellow';
  ctx.fillText('Yellow Canvas', 10, 120);
})
</script>

<style scoped>
#container {
  text-align: center;

  position: absolute;
  left: 0;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
}

#container strong {
  font-size: 20px;
  line-height: 26px;
}

#container p {
  font-size: 16px;
  line-height: 22px;

  color: #8c8c8c;

  margin: 0;
}

#container a {
  text-decoration: none;
}
</style>
