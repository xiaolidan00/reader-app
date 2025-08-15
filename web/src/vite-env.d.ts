/// <reference types="vite/client" />
declare interface Window {
  Android: any;
  AndroidResult: (eventName: string, result: any) => void;
}
