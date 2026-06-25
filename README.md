# @autorunify/capacitor-bleprinter

ble printer plugin

## Install

To use npm

```bash
npm install @autorunify/capacitor-bleprinter
````

To use yarn

```bash
yarn add @autorunify/capacitor-bleprinter
```

Sync native files

```bash
npx cap sync
```

## API

<docgen-index>

* [`checkPermissions()`](#checkpermissions)
* [`requestPermissions()`](#requestpermissions)
* [`isBluetoothEnabled()`](#isbluetoothenabled)
* [`init(...)`](#init)
* [`kill()`](#kill)
* [`devices(...)`](#devices)
* [`isConnected()`](#isconnected)
* [`connect(...)`](#connect)
* [`disconnect(...)`](#disconnect)
* [`printImage(...)`](#printimage)
* [`addListener('state', ...)`](#addlistenerstate-)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### checkPermissions()

```typescript
checkPermissions() => Promise<Permissions>
```

**Returns:** <code>Promise&lt;<a href="#permissions">Permissions</a>&gt;</code>

--------------------


### requestPermissions()

```typescript
requestPermissions() => Promise<Permissions>
```

**Returns:** <code>Promise&lt;<a href="#permissions">Permissions</a>&gt;</code>

--------------------


### isBluetoothEnabled()

```typescript
isBluetoothEnabled() => Promise<BluetoothState>
```

**Returns:** <code>Promise&lt;<a href="#bluetoothstate">BluetoothState</a>&gt;</code>

--------------------


### init(...)

```typescript
init(options: InitOptions) => Promise<void>
```

| Param         | Type                                                |
| ------------- | --------------------------------------------------- |
| **`options`** | <code><a href="#initoptions">InitOptions</a></code> |

--------------------


### kill()

```typescript
kill() => Promise<void>
```

--------------------


### devices(...)

```typescript
devices(options?: ScanOptions | undefined) => Promise<ScanResults>
```

| Param         | Type                                                |
| ------------- | --------------------------------------------------- |
| **`options`** | <code><a href="#scanoptions">ScanOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#scanresults">ScanResults</a>&gt;</code>

--------------------


### isConnected()

```typescript
isConnected() => Promise<ConnectionState>
```

**Returns:** <code>Promise&lt;<a href="#connectionstate">ConnectionState</a>&gt;</code>

--------------------


### connect(...)

```typescript
connect(options: ConnectOptions) => Promise<void>
```

| Param         | Type                                                      |
| ------------- | --------------------------------------------------------- |
| **`options`** | <code><a href="#connectoptions">ConnectOptions</a></code> |

--------------------


### disconnect(...)

```typescript
disconnect(options?: TimeoutOptions | undefined) => Promise<void>
```

| Param         | Type                                                      |
| ------------- | --------------------------------------------------------- |
| **`options`** | <code><a href="#timeoutoptions">TimeoutOptions</a></code> |

--------------------


### printImage(...)

```typescript
printImage(options: PrintImageOptions) => Promise<PrintImageResults>
```

| Param         | Type                                                            |
| ------------- | --------------------------------------------------------------- |
| **`options`** | <code><a href="#printimageoptions">PrintImageOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#printimageresults">PrintImageResults</a>&gt;</code>

--------------------


### addListener('state', ...)

```typescript
addListener(event: 'state', callback: (e: OnStateChangeEvent) => void) => Promise<PluginListenerHandle>
```

| Param          | Type                                                                              |
| -------------- | --------------------------------------------------------------------------------- |
| **`event`**    | <code>'state'</code>                                                              |
| **`callback`** | <code>(e: <a href="#onstatechangeevent">OnStateChangeEvent</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### Interfaces


#### BluetoothState

| Prop          | Type                 |
| ------------- | -------------------- |
| **`enabled`** | <code>boolean</code> |


#### InitOptions

| Prop               | Type                      |
| ------------------ | ------------------------- |
| **`manufacturer`** | <code>'DotHanTech'</code> |


#### ScanResults

| Prop          | Type                                                                              |
| ------------- | --------------------------------------------------------------------------------- |
| **`devices`** | <code><a href="#array">Array</a>&lt;<a href="#bledevice">BleDevice</a>&gt;</code> |


#### Array

| Prop         | Type                | Description                                                                                            |
| ------------ | ------------------- | ------------------------------------------------------------------------------------------------------ |
| **`length`** | <code>number</code> | Gets or sets the length of the array. This is a number one higher than the highest index in the array. |

| Method             | Signature                                                                                                                     | Description                                                                                                                                                                                                                                 |
| ------------------ | ----------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **toString**       | () =&gt; string                                                                                                               | Returns a string representation of an array.                                                                                                                                                                                                |
| **toLocaleString** | () =&gt; string                                                                                                               | Returns a string representation of an array. The elements are converted to string using their toLocalString methods.                                                                                                                        |
| **pop**            | () =&gt; T \| undefined                                                                                                       | Removes the last element from an array and returns it. If the array is empty, undefined is returned and the array is not modified.                                                                                                          |
| **push**           | (...items: T[]) =&gt; number                                                                                                  | Appends new elements to the end of an array, and returns the new length of the array.                                                                                                                                                       |
| **concat**         | (...items: <a href="#concatarray">ConcatArray</a>&lt;T&gt;[]) =&gt; T[]                                                       | Combines two or more arrays. This method returns a new array without modifying any existing arrays.                                                                                                                                         |
| **concat**         | (...items: (T \| <a href="#concatarray">ConcatArray</a>&lt;T&gt;)[]) =&gt; T[]                                                | Combines two or more arrays. This method returns a new array without modifying any existing arrays.                                                                                                                                         |
| **join**           | (separator?: string \| undefined) =&gt; string                                                                                | Adds all the elements of an array into a string, separated by the specified separator string.                                                                                                                                               |
| **reverse**        | () =&gt; T[]                                                                                                                  | Reverses the elements in an array in place. This method mutates the array and returns a reference to the same array.                                                                                                                        |
| **shift**          | () =&gt; T \| undefined                                                                                                       | Removes the first element from an array and returns it. If the array is empty, undefined is returned and the array is not modified.                                                                                                         |
| **slice**          | (start?: number \| undefined, end?: number \| undefined) =&gt; T[]                                                            | Returns a copy of a section of an array. For both start and end, a negative index can be used to indicate an offset from the end of the array. For example, -2 refers to the second to last element of the array.                           |
| **sort**           | (compareFn?: ((a: T, b: T) =&gt; number) \| undefined) =&gt; this                                                             | Sorts an array in place. This method mutates the array and returns a reference to the same array.                                                                                                                                           |
| **splice**         | (start: number, deleteCount?: number \| undefined) =&gt; T[]                                                                  | Removes elements from an array and, if necessary, inserts new elements in their place, returning the deleted elements.                                                                                                                      |
| **splice**         | (start: number, deleteCount: number, ...items: T[]) =&gt; T[]                                                                 | Removes elements from an array and, if necessary, inserts new elements in their place, returning the deleted elements.                                                                                                                      |
| **unshift**        | (...items: T[]) =&gt; number                                                                                                  | Inserts new elements at the start of an array, and returns the new length of the array.                                                                                                                                                     |
| **indexOf**        | (searchElement: T, fromIndex?: number \| undefined) =&gt; number                                                              | Returns the index of the first occurrence of a value in an array, or -1 if it is not present.                                                                                                                                               |
| **lastIndexOf**    | (searchElement: T, fromIndex?: number \| undefined) =&gt; number                                                              | Returns the index of the last occurrence of a specified value in an array, or -1 if it is not present.                                                                                                                                      |
| **every**          | &lt;S extends T&gt;(predicate: (value: T, index: number, array: T[]) =&gt; value is S, thisArg?: any) =&gt; this is S[]       | Determines whether all the members of an array satisfy the specified test.                                                                                                                                                                  |
| **every**          | (predicate: (value: T, index: number, array: T[]) =&gt; unknown, thisArg?: any) =&gt; boolean                                 | Determines whether all the members of an array satisfy the specified test.                                                                                                                                                                  |
| **some**           | (predicate: (value: T, index: number, array: T[]) =&gt; unknown, thisArg?: any) =&gt; boolean                                 | Determines whether the specified callback function returns true for any element of an array.                                                                                                                                                |
| **forEach**        | (callbackfn: (value: T, index: number, array: T[]) =&gt; void, thisArg?: any) =&gt; void                                      | Performs the specified action for each element in an array.                                                                                                                                                                                 |
| **map**            | &lt;U&gt;(callbackfn: (value: T, index: number, array: T[]) =&gt; U, thisArg?: any) =&gt; U[]                                 | Calls a defined callback function on each element of an array, and returns an array that contains the results.                                                                                                                              |
| **filter**         | &lt;S extends T&gt;(predicate: (value: T, index: number, array: T[]) =&gt; value is S, thisArg?: any) =&gt; S[]               | Returns the elements of an array that meet the condition specified in a callback function.                                                                                                                                                  |
| **filter**         | (predicate: (value: T, index: number, array: T[]) =&gt; unknown, thisArg?: any) =&gt; T[]                                     | Returns the elements of an array that meet the condition specified in a callback function.                                                                                                                                                  |
| **reduce**         | (callbackfn: (previousValue: T, currentValue: T, currentIndex: number, array: T[]) =&gt; T) =&gt; T                           | Calls the specified callback function for all the elements in an array. The return value of the callback function is the accumulated result, and is provided as an argument in the next call to the callback function.                      |
| **reduce**         | (callbackfn: (previousValue: T, currentValue: T, currentIndex: number, array: T[]) =&gt; T, initialValue: T) =&gt; T          |                                                                                                                                                                                                                                             |
| **reduce**         | &lt;U&gt;(callbackfn: (previousValue: U, currentValue: T, currentIndex: number, array: T[]) =&gt; U, initialValue: U) =&gt; U | Calls the specified callback function for all the elements in an array. The return value of the callback function is the accumulated result, and is provided as an argument in the next call to the callback function.                      |
| **reduceRight**    | (callbackfn: (previousValue: T, currentValue: T, currentIndex: number, array: T[]) =&gt; T) =&gt; T                           | Calls the specified callback function for all the elements in an array, in descending order. The return value of the callback function is the accumulated result, and is provided as an argument in the next call to the callback function. |
| **reduceRight**    | (callbackfn: (previousValue: T, currentValue: T, currentIndex: number, array: T[]) =&gt; T, initialValue: T) =&gt; T          |                                                                                                                                                                                                                                             |
| **reduceRight**    | &lt;U&gt;(callbackfn: (previousValue: U, currentValue: T, currentIndex: number, array: T[]) =&gt; U, initialValue: U) =&gt; U | Calls the specified callback function for all the elements in an array, in descending order. The return value of the callback function is the accumulated result, and is provided as an argument in the next call to the callback function. |


#### ConcatArray

| Prop         | Type                |
| ------------ | ------------------- |
| **`length`** | <code>number</code> |

| Method    | Signature                                                          |
| --------- | ------------------------------------------------------------------ |
| **join**  | (separator?: string \| undefined) =&gt; string                     |
| **slice** | (start?: number \| undefined, end?: number \| undefined) =&gt; T[] |


#### BleDevice

| Prop          | Type                |
| ------------- | ------------------- |
| **`address`** | <code>string</code> |
| **`name`**    | <code>string</code> |
| **`rssi`**    | <code>number</code> |


#### ScanOptions

| Prop      | Type                |
| --------- | ------------------- |
| **`max`** | <code>number</code> |


#### ConnectionState

| Prop            | Type                 |
| --------------- | -------------------- |
| **`connected`** | <code>boolean</code> |


#### ConnectOptions

| Prop          | Type                |
| ------------- | ------------------- |
| **`address`** | <code>string</code> |


#### TimeoutOptions

| Prop          | Type                |
| ------------- | ------------------- |
| **`timeout`** | <code>number</code> |


#### PrintImageResults

| Prop        | Type                               |
| ----------- | ---------------------------------- |
| **`state`** | <code>'success' \| 'failed'</code> |


#### PrintImageOptions

| Prop            | Type                        |
| --------------- | --------------------------- |
| **`scale`**     | <code>number</code>         |
| **`width`**     | <code>number</code>         |
| **`height`**    | <code>number</code>         |
| **`imageData`** | <code>ImageDataArray</code> |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |


#### OnStateChangeEvent

| Prop         | Type                                  |
| ------------ | ------------------------------------- |
| **`action`** | <code>'enabled' \| 'connected'</code> |
| **`state`**  | <code>boolean</code>                  |


### Type Aliases


#### Permissions

<code>{ [key in PermissionKey]: <a href="#permissionstate">PermissionState</a>; }</code>


#### PermissionKey

<code>"android.permission.BLUETOOTH" | "android.permission.BLUETOOTH_ADMIN" | "android.permission.ACCESS_COARSE_LOCATION" | "android.permission.ACCESS_FINE_LOCATION" | "android.permission.BLUETOOTH_SCAN" | "android.permission.BLUETOOTH_CONNECT"</code>


#### PermissionState

<code>'prompt' | 'prompt-with-rationale' | 'granted' | 'denied'</code>

</docgen-api>
