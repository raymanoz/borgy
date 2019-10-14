import * as React from "react";
import "./Button.css";

export interface Props {
    intensity: number;
    selected: boolean;
}

const Button = ({ intensity, selected }: Props) => {
    return (
        <div className="col">
            <div className={`borg-button btn btn-primary ${selected ? "borg-button-selected" : ""}`}>{intensity}</div>
        </div>
    );
};

export default Button;
