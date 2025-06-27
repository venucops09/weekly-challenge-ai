## 🛍️ Simple ecommerce cart application [![CircleCI](https://circleci.com/gh/jeffersonRibeiro/react-shopping-cart.svg?style=svg)](https://circleci.com/gh/jeffersonRibeiro/react-shopping-cart)

<p align="center">

  <img src="./readme-banner.png">
</p>

## Basic Overview - [Live Demo](https://react-shopping-cart-67954.firebaseapp.com/)

<p align="left">

  <img src="./work-in-the-netherlands.png" width="380" height="90">
</p>

✈️ [Follow Jeremy Akeze](https://www.linkedin.com/in/jeremy-akeze-9542b396/)

This simple shopping cart prototype shows how React with Typescript, React hooks, react Context and Styled Components can be used to build a friendly user experience with instant visual updates and scaleable code in ecommerce applications.

#### Features

- Add and remove products from the floating cart using Context Api
- Filter products by available sizes using Context Api
- Responsive design

<!--
## Getting started

Try playing with the code on CodeSandbox :)

[![Edit app](https://codesandbox.io/static/img/play-codesandbox.svg)](https://codesandbox.io/s/74rykw70qq)
 -->

## Build/Run

#### Requirements

- Node.js
- NPM

```javascript

/* First, Install the needed packages */
npm install

/* Then start the React app */
npm start

/* To run the tests */
npm run test

```

### Copyright and license

The MIT License (MIT). Please see License File for more information.

<br/>
<br/>

<p align="center"><img src="http://www.jeffersonribeiro.com/assets/img/apple-icon-180x180.png" width="35" height="35"/></p>
<p align="center">
<sub>A little project by <a href="http://www.jeffersonribeiro.com/">Jefferson Ribeiro</a></sub>
</p>

# Debugging & Performance Tools

## React DevTools

- Install the React DevTools browser extension (Chrome/Firefox).
- Use the Components and Profiler tabs to inspect component state, props, and performance.

## Linting & Formatting

- Run `npm run lint` to check for code quality issues.
- Run `npm run format` to auto-format code with Prettier.

## Dependency Checks

- Run `npm run audit` to check for security vulnerabilities.
- Run `npm run outdated` to see outdated dependencies.

## Performance Profiling

- Use the React Profiler (in DevTools) to identify slow components and unnecessary re-renders.
- Use Lighthouse (in Chrome DevTools) for overall performance and accessibility audits.

# Testing & Debugging

## Running Tests

- Run `npm test` to execute all unit and integration tests.
- Test files are located alongside their components (e.g., `ProductList.test.js`, `CartProduct.test.tsx`, `ErrorBoundary.test.jsx`).
- Tests cover loading, error, empty, and normal states for key components.

## Debugging

- Use React DevTools for inspecting component state and props.
- Use the Profiler tab in DevTools to analyze performance.
- Error boundaries are in place to catch and display UI errors gracefully.
- Check the browser console for error logs and stack traces.

## Common Issues

- If you see "Something went wrong" in the UI, an error was caught by the ErrorBoundary. Check the console for details.
- If tests fail, review the error message and stack trace for the failing test.

## Useful Scripts

- `npm run lint` — Check code quality
- `npm run format` — Auto-format code
- `npm run audit` — Check for security vulnerabilities
- `npm run outdated` — List outdated dependencies

---
