import * as React from 'react';
import * as enzyme from 'enzyme';
import Button from './Button';

it('renders as expected', () => {
   const button = enzyme.shallow(<Button intensity={5} label={"Heavy"} />);
   expect(button.find(".borg-button").text()).toEqual("Heavy")
});